package cn.magic.web.role.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.role.entity.Role;
import cn.magic.web.role.service.RoleService;
import cn.magic.web.sys_user.entity.SysUser;
import cn.magic.web.topic.entity.Topic;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Autowired
    private RoleService roleService;
    //新增
    @PostMapping
    public ResultVo add(@RequestBody Role role) {
        if (roleService.save(role)) {
            return ResultVo.success("新增成功!");
        }
        return ResultVo.error("新增失败!");
    }

    //编辑
    @PutMapping
    public ResultVo edit(@RequestBody Role role) {
        if (roleService.updateById(role)) {
            return ResultVo.success("编辑成功!");
        }
        return ResultVo.error("编辑失败!");
    }

    //删除
    @Transactional
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id) {
        Long count = roleService.getUserCountByRid(id);
        if(count>0){
            return ResultVo.success("删除失败!其下还有多个用户");
        }
        if (roleService.removeById(id)) {
            return ResultVo.success("删除成功!");
        }
        return ResultVo.error("删除失败!");
    }

    //同样集成了搜索
    @GetMapping("/list")
    public ResultVo getList(@RequestParam String roleName){
        List<Role> list = roleService.getRoleListWithUserCount(roleName);
        return ResultVo.success("查询成功",list);
    }

    // 查询单个角色信息
    @GetMapping("/{rid}")
    public ResultVo getRoleById(@PathVariable long rid) {
        Role role = roleService.getById(rid);
        role.setUserCount(roleService.getUserCountByRid(rid));
        return role != null
                ? ResultVo.success("查询成功", role)
                : ResultVo.error("标签不存在!", HttpStatus.NOT_FOUND);
    }
}
