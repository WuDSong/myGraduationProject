package cn.magic.web.webSocket.controller;

import cn.magic.web.sys_message.entity.SysMessage;
import cn.magic.web.sys_message.service.SysMessageService;
import cn.magic.web.webSocket.entity.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final SysMessageService messageService;

    public MessageController(SimpMessagingTemplate messagingTemplate,
                             SysMessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    /**
     * 推送未读消息给用户
     */
    public void pushUnreadMessages(Long userId) {
        // 1. 查询用户未读消息
        List<SysMessage> unreadMessages = messageService.getUnreadMessages(userId);
        // 2. 通过WebSocket推送
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/messages",
                unreadMessages
        );
        // 3. 标记为已读（可选）
        messageService.markMessagesAsRead(unreadMessages);
    }
}
