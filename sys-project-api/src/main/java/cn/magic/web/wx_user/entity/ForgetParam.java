package cn.magic.web.wx_user.entity;

import lombok.Data;

@Data
public class ForgetParam {
    private String email;
    private String username;
    private String password; // 新密码
}
