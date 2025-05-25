package cn.magic.web.role.service.impl;

import cn.magic.web.role.entity.AssignMenuParams;
import cn.magic.web.role.entity.Role;
import cn.magic.web.role.mapper.RoleMapper;
import cn.magic.web.role.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Override
    public List<Role> getRoleListWithUserCount(String roleName) {
        return this.baseMapper.getRoleListWithUserCount(roleName);
    }

    @Override
    public Long getUserCountByRid(Long rid) {
        return this.baseMapper.getUserCountByRid(rid);
    }

    @Override
    public boolean saveMenu(AssignMenuParams params) {
        // 先删除 所有权限
        this.baseMapper.delRoleAllMenu(params.getRid());
        // 后添加
        boolean insert =this.baseMapper.addRoleMenus(params.getRid(),params.getMenuIds());
        return insert;
    }
}
