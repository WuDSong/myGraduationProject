package cn.magic.web.role.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.role.entity.Role;
import cn.magic.web.role.service.RoleService;
import cn.magic.web.sys_user.entity.SysUser;
import cn.magic.web.sys_user.service.SysUserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public ResultVo getAllList(){
        List<Role> list = roleService.list();
        return ResultVo.success("查询成功",list);
    }

    // 查询单个角色信息
    @GetMapping("/{rid}")
    public ResultVo getRoleById(@PathVariable Integer rid) {
        Role role = roleService.getById(rid);
        return role != null
                ? ResultVo.success("查询成功", role)
                : ResultVo.error("标签不存在!", HttpStatus.NOT_FOUND);
    }
}
