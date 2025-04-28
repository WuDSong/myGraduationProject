package cn.magic.web.role.service.impl;

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
}
