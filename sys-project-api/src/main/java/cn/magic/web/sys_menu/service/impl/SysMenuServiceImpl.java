package cn.magic.web.sys_menu.service.impl;

import cn.magic.web.sys_menu.entity.SysMenu;
import cn.magic.web.sys_menu.mapper.SysMenuMapper;
import cn.magic.web.sys_menu.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    @Override
    public List<SysMenu> getMenuByUserRoleId(Long rid) {
        return this.baseMapper.getMenuByUserId(rid);
    }
}
