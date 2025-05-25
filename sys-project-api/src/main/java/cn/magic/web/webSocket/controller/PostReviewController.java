package cn.magic.web.webSocket.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.post.entity.Post;
import cn.magic.web.post.service.PostService;
import cn.magic.web.webSocket.entity.PostUpdateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

// 后台使用
@Controller
public class PostReviewController {
    // 处理客户端发送到/app/(MessageMapping)的消息   ,并广播到/topic/postUpdates
    @Autowired
    private PostService postService;

    @MessageMapping("/lockPost")
    @SendTo("/topic/postUpdates")  //	SimpMessagingTemplate 可以用在任何地方  @SendTo只能在控制类
    public ResultVo lockPost(Long postId) {
        System.out.println("锁定");
        // 更新其他的版本号
        return ResultVo.error("对帖子锁定失败,当前帖子已经被锁定");
    }
}
