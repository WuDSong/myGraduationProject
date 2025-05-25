package cn.magic.web.comment.controller;
import cn.magic.utils.ResultVo;
import cn.magic.web.comment.entity.Comment;
import cn.magic.web.comment.entity.CommentParam;
import cn.magic.web.comment.service.CommentService;
import cn.magic.web.like.service.LikeService;
import cn.magic.web.post.entity.Post;
import cn.magic.web.wx_user.entity.WxUser;
import cn.magic.web.wx_user.service.WxUserService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private WxUserService wxUserService;

    @Autowired
    private LikeService likeService;

    //新增
    @Transactional
    @PostMapping
    public ResultVo add(@RequestBody Comment comment) {
        // 现在 comment 大部分 都是自己的数据 除了depth path是父亲的，所以要更新
        if (!commentService.save(comment)) {
            return ResultVo.error("新增失败!");
        }
        Long currentCommentId = comment.getCommentId();
        if (comment.getParentId() == null) {
            // 处理顶层评论 主要是初始化
            comment.setRootId(currentCommentId);
            comment.setPath(String.valueOf(currentCommentId));
            comment.setDepth(0L);
        } else {
            // 子评论 更新depth path
            comment.setPath(comment.getPath()+'/'+currentCommentId);
            comment.setDepth(comment.getDepth()+1);
        }
        // 更新 path 和 root_id 到数据库
        commentService.updateById(comment);
        return ResultVo.success("新增成功!");
    }

    //编辑
    @PutMapping
    public ResultVo edit(@RequestBody Comment comment) {
        if (commentService.updateById(comment)) {
            return ResultVo.success("编辑成功!");
        }
        return ResultVo.error("编辑失败!");
    }

    //删除 （伪删除）
    @Transactional
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id) {
/* 直接删除还需要更新孩子path等等 所以采用逻辑删除
        if (commentService.removeById(id)) {
           return ResultVo.success("删除成功!");
       }
*/
        Comment comment = commentService.getById(id);
        comment.setStatus("deleted");
        if (commentService.updateById(comment)) {
            return ResultVo.success("删除成功!");
        }
        return ResultVo.error("删除失败!");
    }

    //分页获取帖子 顶级评论 (带一个子评论)  (标记自己是否点赞)
    /** @param commentParam 必须参数：页大小,当前页号,当前postID,当前登录userId（判断是否喜欢） */
    @GetMapping("/list")
    public ResultVo getTopCommentList(CommentParam commentParam){
        IPage<Comment> page = new Page<>(commentParam.getCurPage(),commentParam.getPageSize());
        QueryWrapper<Comment> query = new QueryWrapper<>();
        query.lambda().eq(Comment::getPostId,commentParam.getPostId()).isNull(Comment::getParentId)
                .eq(Comment::getStatus,"normal");
        IPage<Comment> commentIPage = commentService.page(page, query);
        // 数据处理
        List<Comment> topList =commentIPage.getRecords();
        for (Comment comment: topList){
            // 顶级评论的用户数据
            WxUser user=wxUserService.getById(comment.getAuthorId());
            comment.setUsername(user.getUsername());
            comment.setAvatarUrl(user.getAvatarUrl());
            // 当前登录用户喜欢吗？                   当前登录用户               当前顶级评论
            if(likeService.isLikeTheComment(commentParam.getUserId(),comment.getCommentId()))
                comment.setLiked(true);
            // 随便找一个正常孩子
            QueryWrapper<Comment> childQuery = new QueryWrapper<>();
            childQuery.lambda().eq(Comment::getParentId,comment.getCommentId()).eq(Comment::getStatus,"normal")
                    .last("LIMIT 1");
            Comment child= commentService.getOne(childQuery);
            // 顶级评论的孩子的用户数据
            if(child!=null){
                WxUser childUser=wxUserService.getById(child.getAuthorId());
                child.setUsername(childUser.getUsername());
                List <Comment> list =new ArrayList<>();
                list.add(child);
                comment.setChildren(list);
            }else comment.setChildren(new ArrayList<>());

        }
        return ResultVo.success("查询成功", commentIPage);
    }

    //分页查找 某条评论的（亲的）孩子评论 param也就是孩子的父亲
    /**@param param  必须参数：页大小,当前页号,当前postID,当前评论id,当前评论作者id*/
    @GetMapping("/children")
    public ResultVo getChildrenList(CommentParam param){
        IPage<Comment> page = new Page<>(param.getCurPage(),param.getPageSize());
        QueryWrapper<Comment> query = new QueryWrapper<>();
        query.lambda().eq(Comment::getPostId,param.getPostId()).eq(Comment::getParentId,param.getCommentId())
                .eq(Comment::getStatus,"normal");
        IPage<Comment> commentIPage = commentService.page(page, query);
        // 数据处理
        List<Comment> topList =commentIPage.getRecords();
        for (Comment comment: topList){
            // 自己的用户数据
            WxUser user=wxUserService.getById(comment.getAuthorId());
            comment.setUsername(user.getUsername());
            comment.setAvatarUrl(user.getAvatarUrl());
            // 父亲的用户数据
            WxUser parent=wxUserService.getById(param.getAuthorId());
            comment.setParentAuthorUsername(parent.getUsername());
            comment.setParentAuthorAvatarUrl(parent.getAvatarUrl());
        }
        return ResultVo.success("查询成功", commentIPage);
    }

    //分页查找 顶级评论的所有子孙评论（包括用户信息） 按时间排序 param也就是子孙的祖先 当前评论id就是子孙的root
    /**@param param  必须参数：页大小,当前页号,当前postID,当前评论id,当前登录用户id*/
    @GetMapping("/descendantsOfTopComment")
    public ResultVo getDescendantList(CommentParam param){
        Comment currentComment=  commentService.getById(param.getCommentId());  //当前评论
        Comment ancestorComment = commentService.getById(currentComment.getRootId()); //顶级用户信息
        WxUser topCommentUser = wxUserService.getById(ancestorComment.getAuthorId()); //发布当前顶级评论的用户信息 （祖先）

        IPage<Comment> page = new Page<>(param.getCurPage(),param.getPageSize());
        QueryWrapper<Comment> query = new QueryWrapper<>();
        // 按照时间顺序           同一个Post                                 rootId = 顶级评论id
        query.lambda().eq(Comment::getPostId,param.getPostId()).eq(Comment::getRootId,param.getCommentId())
                .isNotNull(Comment::getParentId).eq(Comment::getStatus,"normal").orderByAsc(Comment::getCreatedAt);
        IPage<Comment> commentIPage = commentService.page(page, query);
        // 数据处理
        List<Comment> topList =commentIPage.getRecords();
        for (Comment comment: topList){
            // 自己的用户数据 头像+名字
            WxUser user=wxUserService.getById(comment.getAuthorId());
            comment.setUsername(user.getUsername());
            comment.setAvatarUrl(user.getAvatarUrl());
            // 父亲的用户数据 名字 ， 不可能是祖先  父亲需要判断是否正常
            Long parentCommentId =comment.getParentId();
            Comment parentComment = commentService.getById(parentCommentId);
            if(!parentComment.getStatus().equals("normal")){ // 当前评论的父亲被删除 那就把祖先当父亲
                comment.setParentAuthorUsername(topCommentUser.getUsername());
                comment.setParentAuthorAvatarUrl(topCommentUser.getAvatarUrl());
            }else{// 找爸爸用户消息
                WxUser parentCommentAuthor = wxUserService.getById(parentComment.getAuthorId());
                comment.setParentAuthorUsername(parentCommentAuthor.getUsername());
                comment.setParentAuthorAvatarUrl(parentCommentAuthor.getAvatarUrl());
            }
            //当前登录用户喜欢吗？
            if(likeService.isLikeTheComment(param.getUserId(),comment.getCommentId()))
                comment.setLiked(true);
        }
        return ResultVo.success("查询成功", commentIPage);
    }

    // 获取当前评论的最新孩子
    @GetMapping("/lastChild/{commentId}")
    public ResultVo getLastChild(@PathVariable("commentId") Long commentId){
        QueryWrapper<Comment> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(Comment::getParentId,commentId).eq(Comment::getStatus,"normal").orderByDesc(Comment::getCreatedAt) // 改为降序（最新的在最前）
                .last("LIMIT 1");
        Comment comment=commentService.getOne(queryWrapper);
        if(comment==null)
            return ResultVo.error("请求错误,没有孩子");
        else {
            WxUser user=wxUserService.getById(comment.getAuthorId());
            comment.setUsername(user.getUsername());
            comment.setAvatarUrl(user.getAvatarUrl());
            return ResultVo.success("查找最晚的孩子成功",comment);
        }
    }

    // 统计同一个post的评论数
    @GetMapping("/total/{postId}")
    public ResultVo countPostComment(@PathVariable("postId") Long postId){
        QueryWrapper<Comment> query = new QueryWrapper<>();
        query.lambda().eq(Comment::getPostId,postId).eq(Comment::getStatus,"normal");
        Long count=commentService.count(query);
        return ResultVo.success("查找当前post评论数成功 ",count);
    }

    // 获取我的评论 我的评论和我的父评论
    @GetMapping("/myComment")
    public ResultVo getMyComment(Long userId,Long curPage,Long pageSize){
        IPage<Comment> page = new Page<>(curPage,pageSize);
        QueryWrapper<Comment> query = new QueryWrapper<>();
        query.lambda().eq(Comment::getAuthorId,userId).eq(Comment::getStatus,"normal").orderByDesc(Comment::getCreatedAt);
        IPage<Comment> commentIPage = commentService.page(page, query); //我的评论
        List<Comment> commentList = commentIPage.getRecords();
        for(Comment comment :commentList){
            // 自己的用户数据 头像+名字
            WxUser user=wxUserService.getById(comment.getAuthorId());
            comment.setUsername(user.getUsername());
            comment.setAvatarUrl(user.getAvatarUrl());
            // 父亲的用户数据 名字
            Long parentCommentId =comment.getParentId();
            if(parentCommentId!=null){ // 如果是顶级评论，顶级评论没有父亲
                Comment parentComment = commentService.getById(parentCommentId);
                if(!parentComment.getStatus().equals("normal")){
                    // 认祖先当爹 把顶级评论当上一条评论
                    Comment ancestorComment = commentService.getById(comment.getRootId());
                    WxUser ancestorUser= wxUserService.getById(ancestorComment.getAuthorId());
                    comment.setParentAuthorUsername(ancestorUser.getUsername());
                    comment.setParentAuthorAvatarUrl(ancestorUser.getAvatarUrl());
                    comment.setParentCommentContent(parentComment.getContent());
                }else{
                    WxUser parentCommentAuthor = wxUserService.getById(parentComment.getAuthorId());
                    comment.setParentAuthorUsername(parentCommentAuthor.getUsername());
                    comment.setParentAuthorAvatarUrl(parentCommentAuthor.getAvatarUrl());
                    comment.setParentCommentContent(parentComment.getContent());
                }
            }

        }
        return ResultVo.success("查找我的评论成功 ",commentIPage);
    }
    // 统计正常评论数量
    @GetMapping("/countOfNormal")
    public ResultVo getCountOfNormal(){
        QueryWrapper<Comment> queryWrapper =new QueryWrapper<>();
        queryWrapper.lambda().eq(Comment::getStatus,"normal");
        Long num =commentService.count(queryWrapper);
        return ResultVo.success("统计正常post数量",num);
    }
}
