package cn.magic.web.post.service.impl;

import cn.magic.web.post.entity.Post;
import cn.magic.web.post.entity.PostParam;
import cn.magic.web.post.mapper.PostMapper;
import cn.magic.web.post.service.PostService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<Post> getPostListWithUserInfo(PostParam param) {
        Page<Post> page = new Page<>(param.getCurPage(), param.getPageSize());
        return this.baseMapper.getPostListWithUserInfo(page,param.getTitle());
    }


}