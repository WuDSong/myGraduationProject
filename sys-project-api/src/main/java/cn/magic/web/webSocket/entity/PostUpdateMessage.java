package cn.magic.web.webSocket.entity;

import lombok.Data;

@Data
public class PostUpdateMessage {
    private Long postId;
    private String action; // "create", "update", "delete"
}
