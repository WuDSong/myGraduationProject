package cn.magic.web.like.service.impl;

import cn.magic.web.like.entity.Like;
import cn.magic.web.like.mapper.LikeMapper;
import cn.magic.web.like.service.LikeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements LikeService {
    @Override
    public boolean isLikeTheComment(Long userId, Long commentId) {
        QueryWrapper<Like> query = new QueryWrapper<>();
        //                         类型肯定是评论                      id是自己
        query.lambda().eq(Like::getTargetType,"comment").eq(Like::getUserId,userId)
                .eq(Like::getTargetId,commentId);
        Like one = getOne(query);
        if(one==null) // 表里没有则不喜欢
            return false;
        return true;
    }
}
