package cn.magic.web.wx_user.entity;

import lombok.Data;
//登录成功后返回的数据
@Data
public class LoginVo {
    private Long userId;
    private String email;
    private String username;
    private String accessToken;
    private String refreshToken;
}
