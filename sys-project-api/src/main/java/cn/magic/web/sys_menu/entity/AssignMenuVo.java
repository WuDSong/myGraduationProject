package cn.magic.web.sys_menu.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// 这个类是为用户分配菜单的时的返回值:当前用户只能分配自己有的菜单
@Data
public class AssignMenuVo {
    //当前用户(已经登录用户)的菜单
    List<SysMenu> currentUserMenuTree = new ArrayList<>();
    //回显的数据(查询用户的)被分配的用户的菜单
    private Object[] checkList;
}
