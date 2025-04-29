package cn.magic.web.role.service;

import cn.magic.web.role.entity.AssignMenuParams;
import cn.magic.web.role.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RoleService extends IService<Role> {
    //  获取所有列表，集成了搜索，返回值带有数量
    List<Role> getRoleListWithUserCount(String roleName);
    //  获取当前rid下的用户数量
    Long getUserCountByRid(Long rid);

    boolean saveMenu(AssignMenuParams params);
}
