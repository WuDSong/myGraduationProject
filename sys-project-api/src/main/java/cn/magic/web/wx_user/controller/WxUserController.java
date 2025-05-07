package cn.magic.web.wx_user.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.wx_user.entity.LoginVo;
import cn.magic.web.wx_user.entity.WxUserVo;
import cn.magic.web.wx_user.service.WxUserService;
import cn.magic.web.wx_user.entity.WxUser;
import cn.magic.web.wx_user.entity.WxUserParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wxUser")
public class WxUserController {
    @Autowired
    WxUserService wxUserService;

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
        query.lambda().eq(WxUser::getUsername, user.getUsername()).eq(WxUser::getPassword,
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
            return ResultVo.success("登录成功", vo);
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
        //todo 增加密码加密
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
        if (user == null||!"active".equals(user.getStatus())) return ResultVo.error("当前用户错误");
        WxUserVo vo =new WxUserVo();
        vo.setUserId(user.getUserId());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        return ResultVo.success("查询成功", vo);
    }

    //重置密码
    @PostMapping("/updatePassword")
    public ResultVo updatePassword(@RequestBody WxUser user) {
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

}
