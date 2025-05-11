package cn.magic.web.post.service.impl;

import cn.magic.web.post.entity.Post;
import cn.magic.web.post.entity.PostParam;
import cn.magic.web.post.mapper.PostMapper;
import cn.magic.web.post.service.PostService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public IPage<Post> getPostListWithUserInfo(PostParam param) {
        Page<Post> page = new Page<>(param.getCurPage(), param.getPageSize());
        return this.baseMapper.getPostListWithUserInfo(page,param.getTitle());
    }

    @Override
    @Transactional
    public boolean lockPost(Long id) {
        Post post = getById(id); // SELECT时获取当前version值
        if (post != null && !post.isLocked()) {
            post.setLocked(true);
            return updateById(post); // 自动生成：UPDATE ... SET ..., version=version+1 WHERE id=... AND version=查询时的version
        }
        return false;
    }

    @Override
    @Transactional
    public boolean unlockPost(Long id) {
        Post post = getById(id);
        if (post != null && post.isLocked()) {
            post.setLocked(false);
            return updateById(post);
        }
        return false;
    }

    @Override
    @Transactional
    public boolean approvePost(Long id) {
        Post post = getById(id);
        if (post != null && post.isLocked()) {
            post.setReviewCount(post.getReviewCount()+1);
            post.setStatus("normal");
            post.setLocked(false);
            return updateById(post);
        }
        return false;
    }

    @Override
    @Transactional
    public boolean rejectPost(Long id) {
        Post post = getById(id);
        if (post != null && post.isLocked()) {
            post.setReviewCount(post.getReviewCount()+1);
            post.setStatus("review_rejected");
            post.setLocked(false);
            return updateById(post);
        }
        return false;
    }


}