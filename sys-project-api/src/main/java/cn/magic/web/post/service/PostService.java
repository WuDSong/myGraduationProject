package cn.magic.web.post.service;

import cn.magic.web.post.entity.Post;
import cn.magic.web.post.entity.PostParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PostService extends IService<Post> {
    // 为post 添加 topic
    boolean addTopicForPost(Post post);
    // 通过id删除post
    boolean deletePostById(Long postId);
    // 通过id删除post的话题
    boolean deleteTopicByPostId(Long postId);
    // 分页查找post带用户信息
    IPage<Post> getPostListWithUserInfo(PostParam param);
    //审核post
    public void processAfterBothAsyncMethods(Post post);
    // 审核 图片和文本 processAfterBothAsyncMethods() 会阻塞直到异步任务完成，因此它本质上是一个同步方法（虽然内部调用了异步方法）。

    // 通过id 将post状态设置为审核失败状态
    boolean rejectedPost(Long id);
}