package cn.magic.web.sys_message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_message")
public class SysMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    // 发送者类型 (0=系统, 1=用户, 2=管理员)
    private Integer senderType;
    // 发送者ID
    private Long senderId;
    // 接收者ID (关联用户)
    private Long receiverId;
    // 消息类型 (1=系统通知, 2=私信, 3=评论回复, 4=点赞通知, 5=审核结果)
    private Integer type;
    // 消息标题
    private String title;
    // 消息内容
    private String content;
    // 是否已读 (0=未读, 1=已读)
    private Boolean isRead;
    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    // 关联业务ID
    private Long relatedId;
    // 关联业务类型
    private String relatedType;
}
