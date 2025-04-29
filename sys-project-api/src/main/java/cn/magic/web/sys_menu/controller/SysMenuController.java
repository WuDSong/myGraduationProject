package cn.magic.web.sys_menu.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.role.entity.Role;
import cn.magic.web.role.service.RoleService;
import cn.magic.web.sys_menu.entity.AssignMenuVo;
import cn.magic.web.sys_menu.entity.MakeMenuTreeUtil;
import cn.magic.web.sys_menu.entity.MenuVo;
import cn.magic.web.sys_menu.entity.SysMenu;
import cn.magic.web.sys_menu.service.SysMenuService;
import cn.magic.web.sys_user.entity.SysUser;
import cn.magic.web.sys_user.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sysMenu")
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private RoleService roleService;

    //新增
    @PostMapping
    public ResultVo add(@RequestBody SysMenu sysMenu) {
        if (sysMenu.getMenuType() == 2) { //按钮类型
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

    //查找所有menu列表
    @GetMapping("/allList")
    public ResultVo getAllList() {
        List<SysMenu> list = sysMenuService.list();
        return ResultVo.success("查询所有列表成功", list);
    }

    //查找所有menu列表,返回树形结构
    @GetMapping("/tree")
    public ResultVo getTree() {
        List<SysMenu> list = sysMenuService.list();
        List<SysMenu> tree = MakeMenuTreeUtil.buildMenuTree(list);
        return ResultVo.success("查询所有列表成功", tree);
    }

    //查找所有menu列表,返回树形结构,用于路由,因此少了许多参数
    @GetMapping("/routeTree")
    public ResultVo getRouteTree() {
        List<SysMenu> list = sysMenuService.list();
        List<MenuVo> tree = MakeMenuTreeUtil.makeRouter(list, 0);
        return ResultVo.success("查询所有列表成功", tree);
    }

    //查找某角色的菜单列表（包括按钮）,返回当前用户菜单树 和角色被分配的菜单id数组
    @GetMapping("/getAssignTree")
    public ResultVo getAssignTree(Long userId, Long userRid, Long rid) {
        // userId是当前登录用户的id(已经保存在store) userRid(可选)是用户角色id  rid则是当前要查询的角色id
        // 检查当前用户的信息
        SysUser currentUser = sysUserService.getById(userId);
        Role currentUserRole = roleService.getById(userRid);
        if (currentUser == null || currentUserRole == null || currentUser.getRid() != currentUserRole.getRid()) {
            return ResultVo.error("传入的参数矛盾,请检查");
        }
        if(!currentUser.getIsAdmin().equals("1")){
            return ResultVo.error("只有管理员才拥有权限修改后台用户角色信息");
        }
        // 查找要查找的角色是否存在，是否是管理员
        Role role = roleService.getById(rid);
        if (role == null)
            return ResultVo.error("查找的角色错误");
        // 查询(当前用户的)菜单信息
        List<SysMenu> currentUserMenuList = null;
        // 判断用户是否是管理员，管理员拥有所有的权限。但是管理员不可以删除超级管理员
        if (StringUtils.isNotEmpty(currentUser.getIsAdmin()) && "1".equals(currentUser.getIsAdmin())) {
            currentUserMenuList = sysMenuService.list();
        } else {
            // 根据用户角色查询
            currentUserMenuList = sysMenuService.getMenuByUserId(userRid);
        }
        List<SysMenu> tree = MakeMenuTreeUtil.buildMenuTree(currentUserMenuList); //当前已经登录的用户的菜单树
        List<SysMenu> menuList = null;  //要查询的角色已经被分配的菜单
        menuList = sysMenuService.getMenuByUserId(rid);
        List<Integer> ids = new ArrayList<>();
        // 使用 Optional.ofNullable 方法来处理 menuByUserId 可能为 null 的情况
        // 如果 menuByUserId 为 null，则使用一个空的 ArrayList 替代
        // 然后将这个列表转换为流（Stream）以便进行后续的操作
        Optional.ofNullable(menuList).orElse(new ArrayList<>())
                .stream()
                // 使用 filter 方法过滤掉流中为 null 的元素
                .filter(item -> item != null)
                // 对于流中的每个元素，调用 forEach 方法执行一个操作
                .forEach(item -> {
                    // 将每个 SysMenu 对象的菜单ID添加到 ids 列表中
                    ids.add(item.getMid());
                });
        // 封装返回数据
        AssignMenuVo assignMenuVo = new AssignMenuVo();
        assignMenuVo.setCurrentUserMenuTree(tree);
        assignMenuVo.setCheckList(ids.toArray());
        return ResultVo.success("查找成功", assignMenuVo);
    }
}
