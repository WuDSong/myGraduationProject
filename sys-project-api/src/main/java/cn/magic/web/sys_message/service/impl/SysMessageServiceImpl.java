package cn.magic.web.sys_message.service.impl;

import cn.magic.web.sys_message.entity.SysMessage;
import cn.magic.web.sys_message.mapper.SysMessageMapper;
import cn.magic.web.sys_message.service.SysMessageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SysMessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage> implements SysMessageService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    // 新增推送方法
    public void pushUnreadMessages(Long userId) {
        System.out.println("==== 开始推送消息 ====");
        System.out.println("目标用户: " + userId);
        List<SysMessage> unreadMessages = getUnreadMessages(userId);
        System.out.println("未读消息数量: " + unreadMessages.size());
        // 通过WebSocket推送
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/messages",
                unreadMessages
        );
        System.out.println("推送消息成功");
        // 推送后标记为已读
//        markMessagesAsRead(unreadMessages);
    }
    @Override
    public List<SysMessage> getUnreadMessages(Long userId) { // 获取未读数据
        QueryWrapper<SysMessage> query = new QueryWrapper<>();
        query.lambda()
                .eq(SysMessage::getReceiverId, userId)
                .eq(SysMessage::getIsRead, false)
                .orderByDesc(SysMessage::getCreateTime);
        return this.list(query);
    }

    @Override
    public void markMessagesAsRead(List<SysMessage> messages) {
        List<Long> ids = messages.stream()
                .map(SysMessage::getId)
                .collect(Collectors.toList());

        if (!ids.isEmpty()) {
            this.update(new UpdateWrapper<SysMessage>()
                    .lambda()
                    .set(SysMessage::getIsRead, true)
                    .in(SysMessage::getId, ids));
        }
    }

    @Override
    public void createAndPushSystemMessage(Long userId, String content) {
        System.out.println("==== 开始创建系统消息 ====");
        System.out.println("用户ID: " + userId + ", 内容: " + content);
        // 创建系统消息
        SysMessage message = new SysMessage();
        message.setSenderType(0); // 系统消息
        message.setReceiverId(userId);
        message.setType(1); // 系统通知
        message.setContent(content);
        message.setIsRead(false);
        this.save(message);

        // 实时推送
        pushUnreadMessages(userId);
    }
}
