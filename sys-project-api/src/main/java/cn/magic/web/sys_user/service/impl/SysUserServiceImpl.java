package cn.magic.web.sys_user.service.impl;

import cn.magic.web.sys_user.entity.SysUser;
import cn.magic.web.sys_user.mapper.SysUserMapper;
import cn.magic.web.sys_user.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>implements SysUserService {
}