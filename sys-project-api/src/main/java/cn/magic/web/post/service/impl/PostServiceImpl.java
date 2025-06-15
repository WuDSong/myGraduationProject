package cn.magic.web.post.service.impl;

import cn.magic.baiduReview.service.ImgCensor;
import cn.magic.baiduReview.service.TextCensor;
import cn.magic.web.post.entity.Post;
import cn.magic.web.post.entity.PostParam;
import cn.magic.web.post.mapper.PostMapper;
import cn.magic.web.post.service.PostService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {
    @Autowired
    private ImgCensor imgCensor;
    @Autowired
    private TextCensor textCensor;

    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);


    @Override
    public boolean addTopicForPost(Post post) {
        // 数据库会使用触发器进行更新topic的参与人数
        return this.baseMapper.insertPostTopic(post.getPostId(), post.getTopicIds());
    }

    @Override
    @Transactional
    public boolean deletePostById(Long postId) {
//        先删除关联的数据
//        this.baseMapper.deletePostTopicByPostId(postId);
//        return removeById(postId);
        Post post = getById(postId);
        if(post==null)
            return false;
        post.setStatus("deleted");
        updateById(post);
        return true;
    }
    @Override
    @Transactional
    public boolean rejectedPost(Long id) {
        Post post = getById(id);
        if(post==null)
            return false;
        post.setStatus("review_rejected");
        updateById(post);
        return true;
    }

    @Override
    public boolean deleteTopicByPostId(Long postId) {
        return this.baseMapper.deletePostTopicByPostId(postId);
    }

    @Override
    public IPage<Post> getPostListWithUserInfo(PostParam param) {
        Page<Post> page = new Page<>(param.getCurPage(), param.getPageSize());
        return this.baseMapper.getPostListWithUserInfo(page, param.getTitle());
    }

    // 审核 图片和文本 processAfterBothAsyncMethods() 会阻塞直到异步任务完成，因此它本质上是一个同步方法（虽然内部调用了异步方法）。
//    @Override
    @Transactional // 整个审核流程在一个事务中
    public void processAfterBothAsyncMethods1(Post post) {
        // 百度 Open api qps request limit reached
        //  提取所有需要审核的内容
        String title = post.getTitle();
        String contentText = post.getContentText();
        List<String> images = post.getCoverImages();
        String videoUrl= post.getVideoPath();
        //  创建存储所有异步任务的列表
        List<CompletableFuture<?>> futures = new ArrayList<>();
        //添加文本审核任务
        String queryText=title+contentText;
        CompletableFuture<Boolean> textCheckFuture;
        if(StringUtils.isNotEmpty(queryText)){
            textCheckFuture = textCensor.TextCensor(queryText);
            futures.add(textCheckFuture);
        }else
            textCheckFuture =CompletableFuture.completedFuture(false); //没有内容就是非法

        // 为每张图片创建异步审核任务
        List<CompletableFuture<Boolean>> imageCheckFutures = new ArrayList<>();
        if (images != null && !images.isEmpty() && post.getHasImages()) {
            for (String imageUrl : images) {
                // 当图片 URL 非空时触发审核
                CompletableFuture<Boolean> imageCheckFuture = StringUtils.isNotEmpty(imageUrl) ?
                        imgCensor.ImgCensor(imageUrl) :
                        CompletableFuture.completedFuture(true); //没有图片当然合法
                futures.add(imageCheckFuture);    //任务
                imageCheckFutures.add(imageCheckFuture);
            }
        }
        //检测视频
        if(post.getHasVideo()){
            System.out.println("检测视频");
        }
        // 等待所有审核任务完成 任一图片失败立即终止：使用 anyMatch 快速失败。 并行结果合并：通过 allOf + 流处理优化。
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.whenCompleteAsync((result, ex) -> {
            try {
                if (ex != null) throw ex;
                // 文本结果
                boolean isTextValid = textCheckFuture.get();
                // 图片结果（全部通过才算有效）allMatch检查流中的所有Boolean对象是否均为true。
                boolean isAllImagesValid = imageCheckFutures.stream()
                        .map(CompletableFuture::join)
                        .allMatch(Boolean::booleanValue);
                // 更新状态
                updatePostWithResult(post.getPostId(), isTextValid && isAllImagesValid);
            } catch (Throwable e) {
                logger.error("帖子审核失败 | postId={}", post.getPostId(), e);
                updatePostWithResult(post.getPostId(), false);
            }
        });
    }



    // 审核修改状态
    @Transactional
    protected void updatePostWithResult(Long postId, boolean isApproved) {
        Post post = getById(postId);
        if (post == null) return;
        post.setReviewCount(post.getReviewCount() + 1);
        if (isApproved) {
            post.setStatus("normal");
        } else {
            post.setStatus("review_rejected");
        }
        updateById(post);
    }



    @Override
    @Transactional // 整个审核流程在一个事务中
    public void processAfterBothAsyncMethods(Post post) {
        //  提取所有需要审核的内容
        String title = post.getTitle();
        String contentText = post.getContentText();
        List<String> images = post.getCoverImages();
        String videoUrl= post.getVideoPath();
        // 创建存储所有异步任务的列表
        List<CompletableFuture<?>> futures = new ArrayList<>();
        // 添加文本审核任务 标题和内容一起审核
        String queryText=title+contentText;
        CompletableFuture<Boolean> textCheckFuture;
        if(StringUtils.isNotEmpty(queryText)){
            textCheckFuture = textCensor.TextCensor(queryText);
            futures.add(textCheckFuture);
        }else textCheckFuture =CompletableFuture.completedFuture(false); //没有内容就是非法
        // 为每张图片创建异步审核任务
        List<CompletableFuture<Boolean>> imageCheckFutures = new ArrayList<>();
        if (images != null && !images.isEmpty() && post.getHasImages()) {
            for (String imageUrl : images) {// 当图片 URL 非空时触发审核
                CompletableFuture<Boolean> imageCheckFuture = StringUtils.isNotEmpty(imageUrl) ?
                        imgCensor.ImgCensor(imageUrl) :
                        CompletableFuture.completedFuture(true); //没有图片当然合法
                futures.add(imageCheckFuture);    //任务
                imageCheckFutures.add(imageCheckFuture);
            }
        }
        // 等待所有审核任务完成，任一图片失败立即终止：使用 anyMatch 快速失败。并行结果合并：通过 allOf + 流处理优化。
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(
                new CompletableFuture[0]));
        allFutures.whenCompleteAsync((result, ex) -> {
            try {
                if (ex != null) throw ex;
                boolean isTextValid = textCheckFuture.get(); // 文本结果
                // 图片结果（全部通过才算有效）allMatch检查流中的所有Boolean对象是否均为true。
                boolean isAllImagesValid = imageCheckFutures.stream()
                        .map(CompletableFuture::join).allMatch(Boolean::booleanValue);
                // 更新状态
                updatePostWithResult(post.getPostId(), isTextValid && isAllImagesValid);
            } catch (Throwable e) {
                logger.error("帖子审核失败 | postId={}", post.getPostId(), e);
                updatePostWithResult(post.getPostId(), false);
            }
        });
    }
}

// 为每张图片创建异步审核任务
//        List<CompletableFuture<Boolean>> imageCheckFutures = new ArrayList<>();
//        if (images != null && !images.isEmpty()) {
//            for (String imageUrl : images) {
//                // 当图片 URL 非空时触发审核
//                CompletableFuture<Boolean> imageCheckFuture = StringUtils.isNotEmpty(imageUrl) ?
//                        imgCensor.ImgCensor(imageUrl) :
//                        CompletableFuture.completedFuture(true);
//                futures.add(imageCheckFuture);    //任务
//                imageCheckFutures.add(imageCheckFuture);
//            }
//        }
//CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//        allFutures.thenRun(() -> {
//            try {
//                // 获取文本审核结果
//                boolean isContentSafe = textCheckFuture.get();
//
//                // 获取图片审核结果
//                boolean isAllImagesSafe = true;
//                for (CompletableFuture<Boolean> imageFuture : imageCheckFutures) { //
//                    isAllImagesSafe = isAllImagesSafe && imageFuture.get();
//                }
//                // 根据审核结果更新帖子状态
//                if (isContentSafe && isAllImagesSafe) {
//                    approvePost(post.getPostId());
//                } else {
//                    rejectPost(post.getPostId());
//                }
//
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//                rejectPost(post.getPostId()); // 标记为审核失败
//            }
//        });

