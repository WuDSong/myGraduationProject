package cn.magic.web.wx_user.service.impl;


import cn.magic.web.wx_user.service.WxUserService;
import cn.magic.web.wx_user.entity.WxUser;
import cn.magic.web.wx_user.mapper.WxUserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class WxUserServiceImpl extends ServiceImpl<WxUserMapper, WxUser> implements WxUserService {
}
