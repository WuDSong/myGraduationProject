package cn.magic.web.wx_user.entity;

import lombok.Data;

@Data
public class WxUserVo {
    private Long userId;
    private String username;
    private String email;
    private String avatarUrl;
    private String phone;
}
