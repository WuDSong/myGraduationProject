package cn.magic.web.log.mapper;

import cn.magic.web.log.entity.UserActiveLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserActiveLogMapper extends BaseMapper<UserActiveLog> {
    @Select("SELECT COUNT(DISTINCT user_id) FROM user_active_log WHERE date = #{date}")
    Long getActiveUserCount(String date);

    @Select("SELECT COUNT(*) AS user_count FROM wx_user WHERE DATE(created_at) = #{date}")
    Long getRegisterUserCount(String date);
}
