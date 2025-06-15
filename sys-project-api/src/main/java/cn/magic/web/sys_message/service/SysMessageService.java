package cn.magic.web.sys_message.service;

import cn.magic.web.sys_message.entity.SysMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysMessageService extends IService<SysMessage> {
    List<SysMessage> getUnreadMessages(Long userId);

    void markMessagesAsRead(List<SysMessage> messages);

    void createAndPushSystemMessage(Long userId, String content);
}
