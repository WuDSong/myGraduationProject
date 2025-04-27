package cn.magic.web.sys_user.entity;

import lombok.Data;

@Data
public class SysUserLoginParam {
    private String username;
    private String password;
    private String code;
}
