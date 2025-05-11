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
    // 查找post带用户信息
    IPage<Post> getPostListWithUserInfo(PostParam param);

    public boolean lockPost(Long id);
    public boolean unlockPost(Long id);

    public boolean approvePost(Long id);
    public boolean rejectPost(Long id);
}