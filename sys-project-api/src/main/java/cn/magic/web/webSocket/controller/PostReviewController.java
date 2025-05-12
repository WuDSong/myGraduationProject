package cn.magic.web.webSocket.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.post.service.PostService;
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
        if(postService.lockPost(postId))
            return ResultVo.success("对帖子锁定成功");
        else return ResultVo.success("对帖子锁定失败,当前帖子已经被锁定");
        // 等价于  messagingTemplate.convertAndSend("/topic/postUpdates", resultVo);
    }

    @MessageMapping("/approvePost")
    @SendTo("/topic/postUpdates")
    public boolean approvePost(Long postId) {
        System.out.println("解锁");
        return postService.approvePost(postId);
    }

    @MessageMapping("/rejectPost")
    @SendTo("/topic/postUpdates")
    public boolean rejectPost(Long postId) {
        System.out.println("失败");
        return postService.rejectPost(postId);
    }

    @MessageMapping("/unlockPost")
    @SendTo("/topic/postUpdates")
    public boolean unlockPost(Long postId) {
        System.out.println("通过");
        return postService.unlockPost(postId);
    }
}
