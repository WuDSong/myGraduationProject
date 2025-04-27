package cn.magic.web.sys_menu.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.sys_menu.entity.MakeMenuTreeUtil;
import cn.magic.web.sys_menu.entity.MenuVo;
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
        if(sysMenu.getMenuType()==2){ //按钮类型
            sysMenu.setIcon(null);
            sysMenu.setPath(null);
            sysMenu.setSort(null);
        }
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

    @GetMapping("/tree")
    public ResultVo getTree(){
        List<SysMenu> list=sysMenuService.list();
        List<SysMenu> tree=MakeMenuTreeUtil.buildMenuTree(list);
        return ResultVo.success("查询所有列表成功",tree);
    }

    @GetMapping("/routeTree")
    public ResultVo getRouteTree(){
        List<SysMenu> list=sysMenuService.list();
        List<MenuVo> tree=MakeMenuTreeUtil.makeRouter(list,0);
        return ResultVo.success("查询所有列表成功",tree);
    }

}
