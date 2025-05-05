package cn.magic.web.collect.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.collect.entity.Collect;
import cn.magic.web.collect.entity.CollectParam;
import cn.magic.web.collect.service.CollectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/collect")
public class CollectController {
    @Autowired
    private CollectService collectService;

    //新增
    @Transactional
    @PostMapping
    public ResultVo add(@RequestBody Collect collect) {
        //判断是否收藏
        QueryWrapper<Collect> queryWrapper =new QueryWrapper<>();
        queryWrapper.lambda().eq(Collect::getPostId,collect.getPostId()).eq(Collect::getUserId,collect.getUserId());
        Collect one = collectService.getOne(queryWrapper);
        if(one==null){
            if (collectService.save(collect)) {
                return ResultVo.success("收藏成功");
            }else return ResultVo.error("收藏失败");
        }
        return ResultVo.error("已经收藏,不必重复收藏");
    }

    //删除
    @DeleteMapping
    public ResultVo delete(Long userId,Long postId) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("user_id", userId);
        conditions.put("post_id", postId);
        if (collectService.removeByMap(conditions)) {
            return ResultVo.success("取消收藏成功");
        }
        return ResultVo.error("取消收藏失败");
    }

    // 判断是否已经收藏
    @GetMapping("/check")
    public ResultVo checkCollect(@RequestParam("userId") Long userId,@RequestParam("postId") Long postId) {
        QueryWrapper<Collect> query = new QueryWrapper<>();
        query.lambda().eq(Collect::getUserId,userId).eq(Collect::getPostId,postId);
        Collect one =collectService.getOne(query);
        if(one != null){ //已经收藏
            return ResultVo.success("查询成功",true);
        }else{ //未收藏
            return ResultVo.success("查询成功",false);
        }
    }

    //分页获取
    @GetMapping("/list")
    public ResultVo getList(CollectParam param) {
        IPage<Collect> page = new Page<>(param.getCurPage(), param.getPageSize());
        QueryWrapper<Collect> query = new QueryWrapper<>();
        //查询
        IPage<Collect> list = collectService.page(page, query);
        return ResultVo.success("查询成功", list);
    }
}
