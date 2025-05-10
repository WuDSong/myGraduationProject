package cn.magic.web.webSocket.controller;

import cn.magic.web.webSocket.entity.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    // 处理客户端发送到/app/send的消息,并广播到/topic/postUpdates
    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public Message handleMessage(Message message) {
        System.out.println("收到消息: " + message.getContent());
        return new Message(message.getContent(), "Server");
    }
}
