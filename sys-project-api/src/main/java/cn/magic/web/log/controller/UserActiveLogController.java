package cn.magic.web.log.controller;

import cn.magic.utils.PastDate;
import cn.magic.utils.ResultVo;
import cn.magic.web.log.entity.UserActiveLog;
import cn.magic.web.log.entity.UserCountVo;
import cn.magic.web.log.service.UserActiveLogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/wxUserActive")
public class UserActiveLogController {

    @Autowired
    private UserActiveLogService userActiveLogService;

    @PostMapping
    public ResultVo addUserActiveLog(@RequestBody UserActiveLog userActiveLog) {
        if (userActiveLog.getDate() == null) {
            userActiveLog.setDate(new Date());
            System.out.println("没有时间");
        }
        Boolean flag = false;
        try {
            if (userActiveLogService.save(userActiveLog)) {
                flag = true;
            }
        } catch (Exception e) {
            flag = false;
        } finally {
            if (flag) {
                return ResultVo.success("添加成功");
            } else return ResultVo.success("添加用户日志失败,因为今天用户已经登录");
        }
    }

    // 统计指定日期的活跃用户数
    @GetMapping("/getUserActiveCountByDate")
    public ResultVo getUserActiveCountByDate(@RequestParam String dateStr) {
        try {
            Long count = userActiveLogService.getActiveUserCount(dateStr);
            return ResultVo.success("查询成功", count);
        } catch (Exception e) {
            return ResultVo.error("日期格式错误，请使用 yyyy-MM-dd 格式");
        }
    }

    // 统计最近30天每天活跃用户数
    @GetMapping("/getUserActiveOneMonth")
    public ResultVo getUserActiveOneMonth() {
        List<String> dates = PastDate.getPastDays(30);
        Collections.reverse(dates);
        List<Long> activeCounts = new ArrayList<>();
        List<Long> registerCounts = new ArrayList<>();
        for (String date : dates) {
            QueryWrapper<UserActiveLog> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(UserActiveLog::getDate, date);
            Long count = userActiveLogService.count(queryWrapper);
            activeCounts.add(count);
            count = userActiveLogService.getRegisterUserCount(date);
            registerCounts.add(count);
        }
        UserCountVo vo = new UserCountVo();
        vo.setDates(dates);
        vo.setActiveCounts(activeCounts);
        vo.setRegisterCounts(registerCounts);
        return ResultVo.success("查询成功", vo);
    }
}