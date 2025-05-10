package cn.magic.web.sys_menu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// 目的：构建前端路由配置所需的简化路由树
// @AllArgsConstructor @NoArgsConstructor 生成有参构造和无参构造函数
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuVo { //菜单
    private Integer menuId;
    private String title;
    private String path;
    private String icon;
    private List<MenuVo> children =new ArrayList<>();
    private Integer menuType;
}
