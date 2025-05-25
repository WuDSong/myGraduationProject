package cn.magic.web.report.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.comment.entity.Comment;
import cn.magic.web.comment.service.CommentService;
import cn.magic.web.post.entity.BasePostInfoVo;
import cn.magic.web.post.entity.Post;
import cn.magic.web.post.service.PostService;
import cn.magic.web.report.entity.CountReportVo;
import cn.magic.web.report.entity.Report;
import cn.magic.web.report.entity.ReportCommentVo;
import cn.magic.web.report.entity.ReportPostVo;
import cn.magic.web.report.service.ReportService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    @Autowired
    private ReportService reportService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;

    //新增
    @Transactional
    @PostMapping
    public ResultVo add(@RequestBody Report report) {
        if(report.getTargetType().equals("app")){
            report.setTargetId(0L);
        }
        if(report.getTargetType().equals("post")){
            Post post = postService.getById(report.getTargetId());
            if(post == null)
                return ResultVo.error("新增举报/建议失败!");
            // 不一定非法 先判定是否存在
        }
        if (reportService.save(report)) {
            return ResultVo.success("新增举报/建议成功!");
        }
        return ResultVo.error("新增举报/建议失败!");
    }

    // 举报成立
    @Transactional
    @PutMapping("/resolved")
    public ResultVo resolved(@RequestBody Report report) {
        if(StringUtils.isEmpty(report.getStatus())){
            report.setResult(report.getStatus());
        }
        report.setStatus("resolved");
        report.setHandledAt(new Date());
        if (reportService.updateById(report)) {
            Report r=reportService.getById(report.getReportId());
            if(r.getTargetType().equals("post")){
                postService.rejectedPost(r.getTargetId());
            }
            if(r.getTargetType().equals("comment")){
                commentService.delComment(r.getTargetId());
            }
            return ResultVo.success("report成立");
        }
        return ResultVo.error("report不成立!");
    }

    //驳回  （举报失败）
    @Transactional
    @PutMapping("/rejected")
    public ResultVo rejected(@RequestBody Report report) {
//        还要通知
        if(StringUtils.isEmpty(report.getStatus())){
            report.setResult(report.getStatus());
        }
        report.setStatus("rejected");
        report.setHandledAt(new Date());
        if(reportService.updateById(report)){
            return ResultVo.success("驳回成功!");
        }
        return ResultVo.error("删除失败!");
    }



    // 查找当前待审核的帖子
    @GetMapping("/controversyPost")
    public ResultVo getPendingPostList(Long curPage,Long pageSize){
        // 查找举报的帖子
        IPage<Report> page = new Page<>(curPage, pageSize);
        QueryWrapper<Report> reportQueryWrapper = new QueryWrapper<>();
        reportQueryWrapper.lambda().eq(Report::getTargetType,"post").eq(Report::getStatus,"pending")
                .orderByAsc(Report::getCreatedAt);
        IPage<Report> reportPost = reportService.page(page,reportQueryWrapper);
        List<Report> reportList =reportPost.getRecords();
        // 查找post 公平，不返回用户信息和话题 版区等等
        List<ReportPostVo> reportPostVos = new ArrayList<>();
        for(Report report : reportList){
            ReportPostVo vo = new ReportPostVo();
            vo.setReportId(report.getReportId());
            vo.setReason(report.getReason());
            vo.setImages(report.getImages());
            vo.setCreatedAt(report.getCreatedAt());

            Post post = postService.getById(report.getTargetId());
            if(post!=null){
                vo.setPostId(post.getPostId());
                // 复制基本信息
                BasePostInfoVo postInfo = new BasePostInfoVo();
                postInfo.setPostId(post.getPostId());
                postInfo.setTitle(post.getTitle());
                postInfo.setContentText(post.getContentText());
                postInfo.setCreatedAt(post.getCreatedAt());
                postInfo.setCoverImages(post.getCoverImages());
                postInfo.setContent(post.getContent());
                vo.setBasePostInfoVo(postInfo);
            }
            reportPostVos.add(vo);
        }
        // 构建分页结果
        IPage<ReportPostVo> resultPage = new Page<>(
                reportPost.getCurrent(),
                reportPost.getSize(),
                reportPost.getTotal()
        );
        resultPage.setRecords(reportPostVos);
        return ResultVo.success("查找争议帖子成功",resultPage);
    }




    // 查找当前待审核的评论
    @GetMapping("/comment")
    public ResultVo getReportComment(Long curPage, Long pageSize) {
        // 创建分页对象
        IPage<Report> page = new Page<>(curPage, pageSize);

        // 构建联合查询条件
        QueryWrapper<Report> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Report::getTargetType, "comment")
                .eq(Report::getStatus, "pending")
                .inSql(Report::getTargetId,
                        "SELECT comment_id FROM comment WHERE comment_id IS NOT NULL"); // 确保评论存在

        // 执行查询并获取分页结果
        IPage<Report> reportPage = reportService.page(page, queryWrapper);
        List<Report> reportList = reportPage.getRecords();

        // 转换结果为VO对象
        List<ReportCommentVo> commentVos = reportList.stream()
                .map(report -> {
                    ReportCommentVo vo = new ReportCommentVo();
                    vo.setReportId(report.getReportId());
                    vo.setReason(report.getReason());
                    vo.setImages(report.getImages());
                    vo.setCreatedAt(report.getCreatedAt());

                    // 由于SQL中已确保评论存在，这里可以直接查询
                    Comment comment = commentService.getById(report.getTargetId());
                    vo.setCurrentContent(comment.getContent());
                    vo.setCommentId(comment.getCommentId());

                    return vo;
                })
                .collect(Collectors.toList());

        // 构建最终分页结果
        IPage<ReportCommentVo> resultPage = new Page<>(
                reportPage.getCurrent(),
                reportPage.getSize(),
                reportPage.getTotal()
        );
        resultPage.setRecords(commentVos);

        return ResultVo.success("查找被举报评论成功", resultPage);
    }




    // 查找当前软件建议
    @GetMapping("/app")
    public ResultVo getAppReportList(Long curPage, Long pageSize){
        // 创建分页对象
        IPage<Report> page = new Page<>(curPage, pageSize);

        // 构建联合查询条件
        QueryWrapper<Report> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Report::getTargetType, "app")
                .eq(Report::getStatus, "pending");
        IPage<Report> reportPage = reportService.page(page, queryWrapper);
        return ResultVo.success("查找程序意见成功",reportPage);

    }

    // 查找当前 report数量
    @GetMapping("/getCountByType")
    public ResultVo getCountByType(){
        CountReportVo countReportVo =new CountReportVo();
        QueryWrapper<Report> postQuery = new QueryWrapper<>();
        QueryWrapper<Report> appQuery = new QueryWrapper<>();
        QueryWrapper<Report> commentQuery = new QueryWrapper<>();
        postQuery.lambda().eq(Report::getStatus,"pending").eq(Report::getTargetType,"post");
        appQuery.lambda().eq(Report::getStatus,"pending").eq(Report::getTargetType,"app");
        commentQuery.lambda().eq(Report::getStatus,"pending").eq(Report::getTargetType,"comment");

        countReportVo.setPostCount(reportService.count(postQuery));
        countReportVo.setCommentCount(reportService.count(commentQuery));
        countReportVo.setAppCount(reportService.count(appQuery));

        return ResultVo.success("查找待办事件成功",countReportVo);
    }
}
