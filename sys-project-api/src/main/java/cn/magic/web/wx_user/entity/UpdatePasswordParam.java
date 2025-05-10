package cn.magic.web.wx_user.entity;

import lombok.Data;

@Data
public class UpdatePasswordParam {
    private Long userId;
    private String password;
    private String oldPassword;
}