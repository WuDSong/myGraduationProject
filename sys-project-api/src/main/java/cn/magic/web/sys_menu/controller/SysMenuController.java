package cn.magic.web.sys_menu.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.sys_menu.entity.SysMenu;
import cn.magic.web.sys_menu.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/sysMenu")
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;
    //新增
    @PostMapping
    public ResultVo add(@RequestBody SysMenu sysMenu) {
        //当字段值为空字符串时，MyBatis-Plus会将其作为有效值插入，覆盖数据库默认值。需确保未传递的字段保持为 null 而非空字符串。
        if(sysMenu.getIcon().equals(""))
            sysMenu.setIcon(null);
        if (sysMenuService.save(sysMenu)) {
            return ResultVo.success("新增成功!");
        }
        return ResultVo.error("新增失败!");
    }

    //编辑
    @PutMapping
    public ResultVo edit(@RequestBody SysMenu sysMenu) {
        if (sysMenuService.updateById(sysMenu)) {
            return ResultVo.success("编辑成功!");
        }
        return ResultVo.error("编辑失败!");
    }

    //删除
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id) {
        if (sysMenuService.removeById(id)) {
            return ResultVo.success("删除成功!");
        }
        return ResultVo.error("删除失败!");
    }

    @GetMapping("/allList")
    public ResultVo getAllList(){
        List<SysMenu> list=sysMenuService.list();
        return ResultVo.success("查询所有列表成功",list);
    }

}
