package cn.magic.web.webSocket.entity;

import lombok.Data;

@Data
public class Message {
    private String content;
    private String sender;

    public Message(String content, String sender) {
        this.content = content;
        this.sender = sender;
    }
}
