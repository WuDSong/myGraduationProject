package cn.magic.web.post.service.impl;

import cn.magic.web.post.entity.Post;
import cn.magic.web.post.mapper.PostMapper;
import cn.magic.web.post.service.PostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {
}