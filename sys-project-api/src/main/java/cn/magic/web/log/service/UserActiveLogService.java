package cn.magic.web.log.service;

import cn.magic.web.log.entity.UserActiveLog;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserActiveLogService extends IService<UserActiveLog> {
    // 获取某天的活跃用户数
    Long getActiveUserCount(String date);
    // 获取某天的注册用户数
    Long getRegisterUserCount(String date);
}
