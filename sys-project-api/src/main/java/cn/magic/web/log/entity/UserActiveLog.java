package cn.magic.web.log.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
@TableName("user_active_log")
@Data
public class UserActiveLog {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private Long userId;
}
