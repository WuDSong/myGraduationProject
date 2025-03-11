package cn.magic.web.wx_user.controller;

import cn.magic.utils.ResultVo;

import cn.magic.web.board.entity.BoardParam;

import cn.magic.web.wx_user.entity.WxUser;
import cn.magic.web.wx_user.service.WxUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wxUser")
public class WxUserController {
    @Autowired
    WxUserService wxUserService;

    //新增
    @PostMapping
    public ResultVo add(@RequestBody WxUser wxUser) {
        if (wxUserService.save(wxUser)) {
            return ResultVo.success("新增成功!");
        }
        return ResultVo.error("新增失败!");
    }

    //编辑
    @PutMapping
    public ResultVo edit(@RequestBody WxUser wxUser) {
        if (wxUserService.updateById(wxUser)) {
            return ResultVo.success("编辑成功!");
        }
        return ResultVo.error("编辑失败!");
    }

    //删除
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id) {
        if (wxUserService.removeById(id)) {
            return ResultVo.success("删除成功!");
        }
        return ResultVo.error("删除失败!");
    }

    @GetMapping("/list")
    public ResultVo getAllList(BoardParam param){
        //构造分页对象
        IPage<WxUser> page = new Page<>(param.getCurPage(), param.getPageSize());
        //构造查询条件
        QueryWrapper<WxUser> query = new QueryWrapper<>();
        // 使用 Lambda 表达式构造查询条件
        if (StringUtils.isNotEmpty(param.getName())) { //如果查询的参数Username有值，则进行模糊查找
            query.lambda().like(WxUser::getUsername, param.getName());
        }
        //查询
        IPage<WxUser> list = wxUserService.page(page, query);
        return ResultVo.success("查询成功", list);
    }

}
