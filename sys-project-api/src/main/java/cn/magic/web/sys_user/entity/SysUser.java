package cn.magic.web.sys_user.entity;

import cn.magic.web.role.entity.Role;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long userid;
    private String username;
    private String password;
    private String sex;
    private String status;
    private String isAdmin;
    private String email;
    private Long rid;
    private String phone;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
    @TableField(exist = false)
    private Role role;
}
