package cn.magic.web.log.service.impl;

import cn.magic.web.log.entity.UserActiveLog;
import cn.magic.web.log.mapper.UserActiveLogMapper;
import cn.magic.web.log.service.UserActiveLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserActiveLogServiceImpl extends ServiceImpl<UserActiveLogMapper, UserActiveLog> implements UserActiveLogService {
    @Override
    public Long getActiveUserCount(String date) {
        return this.baseMapper.getActiveUserCount(date);
    }

    @Override
    public Long getRegisterUserCount(String date) {
        return this.baseMapper.getRegisterUserCount(date);
    }


}
