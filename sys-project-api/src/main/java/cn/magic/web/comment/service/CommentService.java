package cn.magic.web.comment.service;

import cn.magic.web.comment.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CommentService extends IService<Comment> {
    // 修改状态为删除
    boolean delComment(Long id);
}
