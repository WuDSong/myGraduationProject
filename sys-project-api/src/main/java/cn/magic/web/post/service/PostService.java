package cn.magic.web.post.service;

import cn.magic.web.post.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PostService extends IService<Post> {
    // 为post 添加 topic
    boolean addTopicForPost(Post post);
    // 通过id删除post
    boolean deletePostById(Long postId);
    // 通过id删除post的话题
    boolean deleteTopicByPostId(Long postId);

}