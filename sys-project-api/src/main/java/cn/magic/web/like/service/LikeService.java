package cn.magic.web.like.service;

import cn.magic.web.like.entity.Like;
import com.baomidou.mybatisplus.extension.service.IService;

public interface LikeService extends IService<Like> {
//  当前用户是否喜欢id为commentId的评论
    boolean isLikeTheComment(Long userId,Long commentId);
}
