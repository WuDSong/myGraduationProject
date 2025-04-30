package cn.magic.web.sys_user.entity;

import lombok.Data;

@Data
public class UpdatePasswordParam {
    private Long userId;
    private String password;
    private String oldPassword;
}