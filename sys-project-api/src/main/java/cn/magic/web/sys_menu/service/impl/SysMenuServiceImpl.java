package cn.magic.web.sys_menu.service.impl;

import cn.magic.web.sys_menu.entity.SysMenu;
import cn.magic.web.sys_menu.mapper.SysMenuMapper;
import cn.magic.web.sys_menu.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
}
