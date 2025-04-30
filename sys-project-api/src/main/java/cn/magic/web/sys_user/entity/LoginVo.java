package cn.magic.web.sys_user.entity;

import cn.magic.web.sys_menu.entity.MenuVo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// 登录的返回值
@Data
public class LoginVo {
    private Long userid;
    private String username;
    private Long rid;
    private List<MenuVo> menuRouterTree = new ArrayList<>(); //路由菜单
    private List<String> codeList = new ArrayList<>(); //菜单代码
}
