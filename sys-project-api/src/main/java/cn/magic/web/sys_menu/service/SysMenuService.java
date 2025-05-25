package cn.magic.web.sys_menu.service;

import cn.magic.web.sys_menu.entity.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
public interface SysMenuService extends IService<SysMenu> {
//    查找用户的menu
    List<SysMenu> getMenuByUserRoleId(Long rid);

    boolean delMenuInRole(Long menuId);
}
