package cn.magic.web.wx_user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("wx_user")
public class WxUser {
    @TableId(type = IdType.AUTO)
    private Long userId;
    private String username;
    private String password;
    private String email;
    private String avatarUrl;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLogin;
    private String status;
    private String phone;
}