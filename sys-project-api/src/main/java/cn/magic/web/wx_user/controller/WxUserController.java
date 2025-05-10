package cn.magic.web.wx_user.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.log.entity.UserActiveLog;
import cn.magic.web.log.service.UserActiveLogService;
import cn.magic.web.wx_user.entity.*;
import cn.magic.web.wx_user.service.WxUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/wxUser")
public class WxUserController {
    @Autowired
    WxUserService wxUserService;
    @Autowired
    private UserActiveLogService userActiveLogService;
    private static final Logger logger = LoggerFactory.getLogger(WxUserController.class);
    //新增&&注册
    @PostMapping
    public ResultVo add(@RequestBody WxUser wxUser) {
        //判断用户是否被占用
        QueryWrapper<WxUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(WxUser::getUsername, wxUser.getUsername());
        WxUser user = wxUserService.getOne(wrapper);
        if (user != null) {
            return ResultVo.error("用户被占用！重新填写！");
        }
        //密码加密
        String pwd = DigestUtils.md5DigestAsHex(wxUser.getPassword().getBytes());
        wxUser.setPassword(pwd);
        //数据处理
        if (wxUser.getAvatarUrl().equals(""))
            wxUser.setAvatarUrl(null);
        if (wxUserService.save(wxUser)) {
            return ResultVo.success("注册成功!");
        }
        return ResultVo.error("注册失败!");
    }

    // 登录
    @PostMapping("/login")
    public ResultVo login(@RequestBody WxUser user) {
        //构造查询条件
        QueryWrapper<WxUser> query = new QueryWrapper<>();
        query.lambda().eq(WxUser::getUsername, user.getUsername()).eq(WxUser::getStatus,"active").eq(WxUser::getPassword,
                DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        WxUser wxUser = wxUserService.getOne(query);
        if (wxUser != null) {
            if (wxUser.getStatus().equals("1")) {
                return ResultVo.error("您的账户被停用，请联系管理员!");
            }
            //返回成功的数据(不直接返回user)
            LoginVo vo = new LoginVo();
            vo.setUsername(wxUser.getUsername());
            vo.setEmail(wxUser.getEmail());
            vo.setUserId(wxUser.getUserId());
            // 更新最后登录时间
            wxUser.setLastLogin(new Date());
            wxUserService.updateById(wxUser);
            //更新用户活跃日志表
            UserActiveLog userActiveLog = new UserActiveLog();
            userActiveLog.setDate(new Date());
            userActiveLog.setUserId(wxUser.getUserId());
            try {
                userActiveLogService.save(userActiveLog);
            }catch (Exception e){
                logger.error("当前用户已被记录活跃时间"+e);
            }
            finally {
                return ResultVo.success("登录成功", vo);
            }
        }
        return ResultVo.error("用户密码或密码错误!");
    }

    //判断用户是否被占用
    @GetMapping("/isOccupied/{username}")
    public ResultVo isOccupied(@PathVariable("username") String username) {
        QueryWrapper<WxUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(WxUser::getUsername, username);
        WxUser wxUser = wxUserService.getOne(wrapper);
        if (wxUser != null) {
            return ResultVo.success("用户被占用！重新填写！", true);
        }
        return ResultVo.success("用户没有被占用", false);
    }

    //编辑
    @PutMapping
    public ResultVo edit(@RequestBody WxUser wxUser) {
        if (wxUserService.updateById(wxUser)) {
            return ResultVo.success("编辑成功!");
        }
        return ResultVo.error("编辑失败!");
    }


    //删除
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id) {
        if (wxUserService.removeById(id)) {
            return ResultVo.success("删除成功!");
        }
        return ResultVo.error("删除失败!");
    }

    @GetMapping("/list")
    public ResultVo getAllList(WxUserParam param) {
        //构造分页对象
        IPage<WxUser> page = new Page<>(param.getCurPage(), param.getPageSize());
        //构造查询条件
        QueryWrapper<WxUser> query = new QueryWrapper<>();
        // 使用 Lambda 表达式构造查询条件
        if (StringUtils.isNotEmpty(param.getUsername())) { //如果查询的参数Username有值，则进行模糊查找
            query.lambda().like(WxUser::getUsername, param.getUsername());
        }
        //查询
        IPage<WxUser> list = wxUserService.page(page, query);
        return ResultVo.success("查询成功", list);
    }

    // 通过用户id 查找用户
    @GetMapping("/{id}")
    public ResultVo getUserById(@PathVariable("id") Long id) {
        WxUser user = wxUserService.getById(id);
        if (user == null || !"active".equals(user.getStatus())) return ResultVo.error("当前用户错误");
        WxUserVo vo = new WxUserVo();
        vo.setUserId(user.getUserId());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        return ResultVo.success("查询成功", vo);
    }

    //小程序忘记密码
    @PostMapping("/forget")
    public ResultVo forget(@RequestBody ForgetParam param){
        //查询用户是否存在
        QueryWrapper<WxUser> query = new QueryWrapper<>();
        query.lambda().eq(WxUser::getUsername,param.getUsername())
                .eq(WxUser::getEmail,param.getEmail());
        WxUser one = wxUserService.getOne(query);
        if(one == null){
            return ResultVo.error("账户或邮箱不正确!");
        }
        //更新条件
        UpdateWrapper<WxUser> update = new UpdateWrapper<>();
        update.lambda().set(WxUser::getPassword,DigestUtils.md5DigestAsHex(param.getPassword().getBytes()))
                .eq(WxUser::getUsername,param.getUsername())
                .eq(WxUser::getEmail,param.getEmail());
        if(wxUserService.update(update)){
            return ResultVo.success("修改密码成功!");
        }
        return ResultVo.error("修改失败!");
    }

    // 后台直接重置密码
    @PostMapping("/resetPassword")
    public ResultVo resetPassword(@RequestBody WxUser user) {
        //默认重置密码为123123
        String pas = "123123";
        UpdateWrapper<WxUser> query = new UpdateWrapper<>();
        query.lambda().set(WxUser::getPassword, DigestUtils.md5DigestAsHex(pas.getBytes()))
                .eq(WxUser::getUserId, user.getUserId());
        if (wxUserService.update(query)) {
            return ResultVo.success("重置成功!");
        }
        return ResultVo.error("重置失败!");
    }

    // 更新密码 , 已经登录
    @PutMapping("/updatePassword")
    public ResultVo updatePassword(@RequestBody UpdatePasswordParam param) {
        System.out.println(param.getOldPassword());
        WxUser user = wxUserService.getById(param.getUserId());
        if(user==null)
            return ResultVo.error("用户非法");
        if (!user.getPassword().equals(DigestUtils.md5DigestAsHex(param.getOldPassword().getBytes())) ) {
            return ResultVo.error("原密码不正确");
        }
        user.setPassword(DigestUtils.md5DigestAsHex(param.getPassword().getBytes()));
        if (wxUserService.updateById(user)) {
            return ResultVo.success("修改密码成功!");
        }
        return ResultVo.error("修改密码失败!");
    }
}
