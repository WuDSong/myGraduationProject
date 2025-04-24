package cn.magic.web.post.service.impl;

import cn.magic.web.post.entity.Post;
import cn.magic.web.post.mapper.PostMapper;
import cn.magic.web.post.service.PostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService{

    @Override
    public boolean addTopicForPost(Post post) {
        return this.baseMapper.insertPostTopic(post.getPostId(),post.getTopicIds());
    }

    @Override
    public boolean deletePostById(Long postId) {
//        先删除关联的数据
        this.baseMapper.deletePostTopicByPostId(postId);
        return removeById(postId);
    }

    @Override
    public boolean deleteTopicByPostId(Long postId) {
        return this.baseMapper.deletePostTopicByPostId(postId);
    }


}