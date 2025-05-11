package cn.magic.web.post.controller;

import cn.magic.utils.ImgChecker;
import cn.magic.utils.ResultVo;
import cn.magic.web.board.entity.Board;
import cn.magic.web.board.service.BoardService;
import cn.magic.web.post.entity.BasePostInfoVo;
import cn.magic.web.post.entity.MyPostVo;
import cn.magic.web.post.entity.Post;
import cn.magic.web.post.entity.PostParam;
import cn.magic.web.post.service.PostService;
import cn.magic.web.topic.entity.Topic;
import cn.magic.web.topic.service.TopicService;
import cn.magic.web.wx_user.entity.WxUser;
import cn.magic.web.wx_user.service.WxUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/post")
public class PostController {
    // 创建 Logger 实例
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    @Autowired
    private PostService postService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private WxUserService wxUserService;

    // 使用Jsoup解析HTML内容获取图片路径
    public List<String> extractImageUrls(String htmlContent) {
        //富文本内的图片已经上传过经过验证的
        List<String> urls = new ArrayList<>();
        Document doc = Jsoup.parse(htmlContent);
        Elements imgs = doc.select("img[src]");
        for (Element img : imgs) {
            String src = img.attr("src");
            if (ImgChecker.isValidImageUrl(src)) { // 验证URL合法性
                urls.add(src);
//                if (urls.size() >= 3) break; // 只记录前三张
            }
        }
        return urls;
    }

    //检测帖子的话题是否有效
    public boolean checkTopicIds(List<Integer> topicIdList) {
        if (topicIdList != null && topicIdList.size() > 0) {
            // 检测每个话题是否存在
            for (Integer topicId : topicIdList) {
                boolean topicExists = topicService.lambdaQuery().eq(Topic::getTopicId, topicId).eq(Topic::getStatus, "active").exists();
                if (!topicExists) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    // 新增帖子 开启事务 包装成一个原子性事务
    @Transactional
    @PostMapping
    public ResultVo addPost(@RequestBody Post post) {
        // 数据处理
        //万一使用的是之前的草稿,要检测话题是否还有效吗？交给审核？ 直接打回！
        boolean boardExists = boardService.lambdaQuery().eq(Board::getBoardId, post.getBoardId()).eq(Board::getStatus, "active").exists();
        if (!boardExists) {
            ResultVo.error("版区不存在或已被封禁");
        }
        if (post.getTopicIds().size() > 0 && !checkTopicIds(post.getTopicIds())) {
            return ResultVo.error("话题不存在或已被封禁");
        }
        // 验证通过
        logger.info(" 验证通过！ 新增一条帖子-ing");
        post.setReviewCount(null);
        // post封面处理
        List<String> imgCoverArrays = extractImageUrls(post.getContent());
        if (imgCoverArrays.size() != 0) { //如果有图片
            post.setHasImages(true);
            post.setCoverImages(imgCoverArrays);
        }
        if (postService.save(post)) {
            //保存了post
            if (post.getTopicIds() != null && post.getTopicIds().size() > 0)
                postService.addTopicForPost(post);
            logger.info("新增一条帖子成功");
            return ResultVo.success("上传帖子成功!");
        }
        return ResultVo.error("上传帖子失败!");
    }

    // 删除帖子:完全删除,先删除post_topic 等等
    @Transactional
    @DeleteMapping("/{postId}")
    public ResultVo deletePost(@PathVariable Long postId) {
        if (postService.deletePostById(postId)) {
            return ResultVo.success("删除成功!");
        }
        return ResultVo.error("删除失败!");
    }
    // 伪删除
    @Transactional
    @DeleteMapping("/del/{postId}")
    public ResultVo delPost(@PathVariable Long postId) {
        Post post = postService.getById(postId);
        post.setStatus("deleted");
        if (postService.updateById(post)) {
            return ResultVo.success("删除成功!");
        }
        return ResultVo.error("删除失败!");
    }

    // 修改帖子
    @Transactional
    @PutMapping
    public ResultVo updatePost(@RequestBody Post post) {
        if (!checkTopicIds(post.getTopicIds())) {
            return ResultVo.error("话题不存在或已被封禁");
        }
        if (postService.updateById(post)) {
            postService.deleteTopicByPostId(post.getPostId());
            postService.addTopicForPost(post);
            return ResultVo.success("编辑成功!");
        }
        return ResultVo.error("编辑失败!");
    }

    // 根据ID查询单个帖子（完整信息），带话题等等
    @GetMapping("/{postId}")
    public ResultVo getPostById(@PathVariable long postId) {
        Post post = postService.getById(postId);
        if (post == null)
            return ResultVo.error("话题不存在");
        post.setTopicList(topicService.getTopicsByPostId(postId));
        WxUser user = wxUserService.getById(post.getAuthorId());
        post.setUsername(user.getUsername());
        post.setAvatarUrl(user.getAvatarUrl());
        return ResultVo.success("查找成功", post);
    }

    // 查询所有帖子(无论状态)，不带话题
    @GetMapping("/allList")
    public ResultVo getAllPosts() {
        List<Post> list = postService.list();
        return ResultVo.success("查询成功", list);
    }

    // 分页查询帖子(无论状态)，不带话题等等其他内容
    @GetMapping("/list")
    public ResultVo getPostsByPage(PostParam postParam) {
        //构造分页对象
        IPage<Post> page = new Page<>(postParam.getCurPage(), postParam.getPageSize());
        //构造查询条件
        QueryWrapper<Post> query = new QueryWrapper<>();
        // 使用 Lambda 表达式构造查询条件
        if (StringUtils.isNotEmpty(postParam.getTitle())) { //如果查询的参数有值，则进行模糊查找
            query.lambda().like(Post::getTitle, postParam.getTitle());
        }
        //查询
        IPage<Post> list = postService.page(page, query);
        return ResultVo.success("查询成功", list);
    }

    // 分页查询帖子(正常状态)(只是帖子)，不带话题等等其他内容 http://localhost:12345/api/post/page?current=1&&size=5
    @GetMapping("/page")
    public Page<Post> getPostsByPage(@RequestParam(defaultValue = "1") long current, @RequestParam(defaultValue = "10") long size) {
//        emm 这种写法,可以避免每次请求都要编写一个类
        Page<Post> page = new Page<>(current, size);
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Post::getStatus, "normal"); //查询已经被审核成功，正常的帖子
        return postService.page(page, queryWrapper);
    }

    // 分页查找正常帖子带话题等等其他所有东西(完整信息的帖子), 1.使用sql查询
    @GetMapping("/fullPostsInfoList")
    public ResultVo getFullPostsInfoList(PostParam param) {
        // 先查带有user信息的Post
        IPage<Post> page = postService.getPostListWithUserInfo(param);
        List<Post> postList = page.getRecords();
        // 调用话题服务查话题
        Optional.ofNullable(postList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null)
                .forEach(item -> {
                    item.setTopicList(topicService.getTopicsByPostId(item.getPostId()));
                });
        return ResultVo.success("查找成功", page);
    }


    // 根据作者ID查询帖子，不带话题
    @GetMapping("/author/{authorId}")
    public List<Post> getPostsByAuthorId(@PathVariable Integer authorId) {
        QueryWrapper<Post> wrapper = new QueryWrapper<>();
        wrapper.eq("author_id", authorId);
        return postService.list(wrapper);
    }


    // 分页查找相同版区的帖子(完整信息的帖子) ,2.尝试使用Mybatis-Plus查询
    @GetMapping("/fullPostInfoInSameBoard")
    public ResultVo getFullPostsInfoListInSameBoard(PostParam param) {
        if (param.getBoardId() == null) {
            throw new IllegalArgumentException("boardId 不能为空");
        }
        //构造条件查询 相同board normal
        IPage<Post> page = new Page<>(param.getCurPage(), param.getPageSize());
        QueryWrapper<Post> query = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(param.getTitle())) { //如果查询的参数有值，则进行模糊查找
            query.lambda().eq(Post::getBoardId, param.getBoardId()).eq(Post::getStatus, "normal")
                    .like(Post::getTitle, param.getTitle());
        } else
            query.lambda().eq(Post::getBoardId, param.getBoardId()).eq(Post::getStatus, "normal");
        IPage<Post> postIPage = postService.page(page, query);
        List<Post> postList = postIPage.getRecords();
        Optional.ofNullable(postList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null)
                .forEach(item -> {
                    //设置
                    WxUser user = wxUserService.getById(item.getAuthorId());
                    item.setUsername(user.getUsername());
                    item.setAvatarUrl(user.getAvatarUrl());
                    //设置话题
                    item.setTopicList(topicService.getTopicsByPostId(item.getPostId()));
                });

        return ResultVo.success("查找成功", page);
    }

    // 分页查询查找待二审的帖子  一审默认执行过了



    //  获取我的帖子 （正常/审核中/审核后）
    @GetMapping("/myPost/{userId}")
    public ResultVo getMyPost(@PathVariable("userId") Long userId) {
        MyPostVo myPostVo = new MyPostVo();

        QueryWrapper<Post> queryNormal = new QueryWrapper<>();
        queryNormal.lambda().eq(Post::getAuthorId,userId).eq(Post::getStatus,"normal").orderByDesc(Post::getCreatedAt);
        List<Post> normalList=postService.list(queryNormal);

        QueryWrapper<Post> queryRejected = new QueryWrapper<>();
        queryRejected.lambda().eq(Post::getAuthorId,userId).eq(Post::getStatus,"review_rejected").orderByDesc(Post::getCreatedAt);
        List<Post> rejectedList=postService.list(queryRejected);

        QueryWrapper<Post> queryPending = new QueryWrapper<>();
        queryPending.lambda().eq(Post::getAuthorId,userId).eq(Post::getStatus,"pending_review").orderByDesc(Post::getCreatedAt);
        List<Post> pendingList=postService.list(queryPending);

        myPostVo.setNormal(normalList);
        myPostVo.setPending_review(pendingList);
        myPostVo.setReview_rejected(rejectedList);

        return ResultVo.success("ok",myPostVo);
    }

    @GetMapping("/myDraftPost")
    public ResultVo getMyDraftPost(Long userId){
        QueryWrapper<Post> queryWrapper =new QueryWrapper<>();
        queryWrapper.lambda().eq(Post::getAuthorId,userId).eq(Post::getStatus,"draft").orderByDesc(Post::getCreatedAt);
        List<Post> list = postService.list(queryWrapper);
        List<BasePostInfoVo> basePostInfoVos =new ArrayList<>();
        for(Post p:list){
            BasePostInfoVo b=new BasePostInfoVo();
            b.setTitle(p.getTitle());
            b.setCreatedAt(p.getCreatedAt());
            b.setContentText(p.getContentText());
            b.setPostId(p.getPostId());
            b.setCoverImages(p.getCoverImages());
            basePostInfoVos.add(b);
        }
        return ResultVo.success("查找我的草稿成功",basePostInfoVos);
    }

}
