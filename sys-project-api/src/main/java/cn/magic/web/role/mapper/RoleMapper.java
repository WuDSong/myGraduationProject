package cn.magic.web.role.mapper;

import cn.magic.web.role.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {
    List<Role> getRoleListWithUserCount(@Param("roleName")String roleName);

    Long getUserCountByRid(@Param("rid")Long rid);
}
