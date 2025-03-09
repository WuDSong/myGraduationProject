package cn.magic.web.sys_menu.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.sys_menu.entity.SysMenu;
import cn.magic.web.sys_menu.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/sysMenu")
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;

    @GetMapping("/allList")
    public ResultVo getAllList(){
        List<SysMenu> list=sysMenuService.list();
        return ResultVo.success("查询所有列表成功",list);
    }

}
