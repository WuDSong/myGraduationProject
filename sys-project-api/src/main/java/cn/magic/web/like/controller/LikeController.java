package cn.magic.web.like.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.collect.entity.Collect;
import cn.magic.web.like.entity.Like;
import cn.magic.web.like.service.LikeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/like")
public class LikeController {
    @Autowired
    private LikeService likeService;

    //新增
    @Transactional
    @PostMapping
    public ResultVo add(@RequestBody Like like) {
        //判断是否点赞 类型 用户名 id 是否相同
        QueryWrapper<Like> queryWrapper =new QueryWrapper<>();
        queryWrapper.lambda().eq(Like::getTargetType,like.getTargetType()).eq(Like::getUserId,like.getUserId())
                .eq(Like::getTargetId,like.getTargetId());
        Like one = likeService.getOne(queryWrapper);
        if(one==null){
            if (likeService.save(like)) {
                Long likeId= like.getLikeId();
                return ResultVo.success("点赞成功",likeId);
            }else return ResultVo.error("点赞失败");
        }
        return ResultVo.error("已经点赞,不必重复点赞");
    }

    //删除
    @DeleteMapping("/{likeId}")
    public ResultVo delete(@PathVariable("likeId")Long likeId) {
        if (likeService.removeById(likeId)) {
            return ResultVo.success("取消点赞成功");
        }
        return ResultVo.error("取消点赞失败");
    }

    @DeleteMapping("/del")
    public ResultVo deleteByLikeInfo(Long userId,String targetType,Long targetId){
        QueryWrapper<Like> queryWrapper =new QueryWrapper<>();
        queryWrapper.lambda().eq(Like::getTargetType,targetType).eq(Like::getUserId,userId)
                .eq(Like::getTargetId,targetId);
        if(likeService.remove(queryWrapper)){
            return ResultVo.success("取消点赞成功");
        }
        return ResultVo.error("取消点赞失败");
    }

    // 判断是否已经点赞
    @GetMapping("/check")
    public ResultVo checkLike(Like like) {
        QueryWrapper<Like> query = new QueryWrapper<>();
        query.lambda().eq(Like::getTargetType,like.getTargetType()).eq(Like::getUserId,like.getUserId())
                .eq(Like::getTargetId,like.getTargetId());
        Like one = likeService.getOne(query);
        if(one != null){ //已经点赞
            return ResultVo.success("查询成功",true);
        }else{ //未收藏
            return ResultVo.success("查询成功",false);
        }
    }

    // 测试端口
    @GetMapping("/test")
    public ResultVo testIsLike(Long userId,Long commentId) {
        Boolean one = likeService.isLikeTheComment(userId,commentId);
        if(one){ //喜欢
            return ResultVo.success("查询成功",true);
        }else{ //不喜欢
            return ResultVo.success("查询成功",false);
        }
    }

}
