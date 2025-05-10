package cn.magic.web.sys_menu.entity;

import java.util.*;

public class MakeMenuTreeUtil {
    // 转换为树形结构
    public static List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        Map<Integer, SysMenu> menuMap = new LinkedHashMap<>();
        // 先处理父节点
        menus.stream()
                .filter(menu -> menu.getParentId() == 0)
                .sorted(Comparator.comparing(SysMenu::getSort))
                .forEach(menu -> {
                    menuMap.put(menu.getMid(), menu);
                    menu.setChildren(new ArrayList<>());
                });

        // 处理子节点
        menus.stream()
                .filter(menu -> menu.getParentId() != 0)
                .sorted(Comparator.comparing(SysMenu::getSort))
                .forEach(menu -> {
                    SysMenu parent = menuMap.get(menu.getParentId());
                    if (parent != null) {
                        parent.getChildren().add(menu);
                    }
                });

        return new ArrayList<>(menuMap.values());
    }

    public static List<MenuVo> makeRouterTree(List<SysMenu> menuList, Integer pid) {
        //构建存放路由数据的容器
        List<MenuVo> list = new ArrayList<>();
        Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                .filter(item -> item != null && item.getParentId().equals(pid))
                .forEach(item -> {
                    MenuVo router = new MenuVo();
                    router.setMenuId(item.getMid());
                    router.setIcon(item.getIcon());
                    router.setTitle(item.getMenuName());
                    router.setPath(item.getPath());
                    router.setMenuType(item.getMenuType());
                    //设置children 递归调用：自己调用自己
                    List<MenuVo> children = makeRouterTree(menuList,item.getMid());
                    router.setChildren(children);
                    list.add(router);
                });
        return list;
    }
}
