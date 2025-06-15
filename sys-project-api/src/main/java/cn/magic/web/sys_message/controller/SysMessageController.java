package cn.magic.web.sys_message.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.like.entity.Like;
import cn.magic.web.sys_message.entity.SysMessage;
import cn.magic.web.sys_message.service.SysMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sysMessage")
public class SysMessageController {
    @Autowired
    private SysMessageService sysMessageService;

    @GetMapping("/test")
    public ResultVo test(){
        sysMessageService.createAndPushSystemMessage(9L,"成功推送");
        return ResultVo.success("测试成功");
    }
    @GetMapping("/getList")
    public ResultVo getList(){
        List<SysMessage> sysMessageList=sysMessageService.list();
        return ResultVo.success("测试成功",sysMessageList);
    }
}
