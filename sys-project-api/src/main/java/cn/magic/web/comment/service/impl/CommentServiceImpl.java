package cn.magic.web.comment.service.impl;

import cn.magic.web.comment.entity.Comment;
import cn.magic.web.comment.mapper.CommentMapper;
import cn.magic.web.comment.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
}
