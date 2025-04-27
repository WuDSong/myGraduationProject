package cn.magic.web.sys_user.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.role.entity.Role;
import cn.magic.web.role.service.RoleService;
import cn.magic.web.sys_user.entity.SysUserLoginParam;
import cn.magic.web.sys_user.entity.LoginVo;
import cn.magic.web.sys_user.entity.SysUser;
import cn.magic.web.sys_user.entity.SysUserParam;
import cn.magic.web.sys_user.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sysUser")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private RoleService roleService;
    private static final Logger logger = LoggerFactory.getLogger(SysUserController.class);
    //新增
    @PostMapping
    public ResultVo add(@RequestBody SysUser sysUser) {
        //判断用户是否被占用
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysUser::getUsername, sysUser.getUsername());
        SysUser user = sysUserService.getOne(wrapper);
        if (user != null) {
            return ResultVo.error("用户被占用！重新填写！");
//            return ResultVo.error();
        }
        if (sysUserService.save(sysUser)) {
            return ResultVo.success("新增成功!");
        }
        return ResultVo.error("新增失败!");
    }

    //编辑
    @PutMapping
    public ResultVo edit(@RequestBody SysUser sysUser) {
        if (sysUserService.updateById(sysUser)) {
            return ResultVo.success("编辑成功!");
        }
        return ResultVo.error("编辑失败!");
    }

    //删除
    @DeleteMapping("/{userId}")
    public ResultVo delete(@PathVariable("userId") Long userId) {
        if (sysUserService.removeById(userId)) {
            return ResultVo.success("删除成功!");
        }
        return ResultVo.error("删除失败!");
    }
    //判断用户是否被占用
    @GetMapping("/isOccupied/{username}")
    public ResultVo isOccupied(@PathVariable("username") String username){
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysUser::getUsername, username);
        SysUser user = sysUserService.getOne(wrapper);
        if (user != null) {
            return ResultVo.success("用户被占用！重新填写！",true);
        }
        return ResultVo.success("用户没有被占用",false);
    }

    //列表和查找
    @GetMapping("/getList")
    public ResultVo getList(SysUserParam param) {
        //构造分页对象
        IPage<SysUser> page = new Page<>(param.getCurPage(), param.getPageSize());
        //构造查询条件
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        // 使用 Lambda 表达式构造查询条件
        if (StringUtils.isNotEmpty(param.getUsername())) { //如果查询的参数Username有值，则进行模糊查找
            query.lambda().like(SysUser::getUsername, param.getUsername());
        }
        //查询
        IPage<SysUser> list = sysUserService.page(page, query);
        //IPage属性："total": 数据库查到总记录数;  "size":页大小;  "current":当前页;  "pages"页号
        if (list.getTotal() > 0) {//有记录
            List<SysUser> tempList = list.getRecords();
            for (SysUser sysUser : tempList) { //页大小
                Long rid = sysUser.getRid();//获取用户角色id
                Role role = roleService.getById(rid);//通过角色id获取角色信息
                sysUser.setRole(role);//设置角色信息
            }
            list.setRecords(tempList);
        }
        return ResultVo.success("查询成功", list);
    }


    //测试函数
    @GetMapping("/getAllList")
    public ResultVo getAllList() {
        IPage<SysUser> page = new Page<>(1, 10);
//        QueryWrapper<Video> query = new QueryWrapper<>();
        IPage<SysUser> list = sysUserService.page(page);
        return ResultVo.success("查询成功", list);
    }

    @PostMapping("/login")
    public ResultVo login(@RequestBody SysUserLoginParam sysUserLoginParam, HttpServletRequest request){
        //获取session里面的code验证码
        HttpSession session = (HttpSession) request.getSession();
        String code = (String)session.getAttribute("code");
        //获取前端传递过来的验证码
        String codeParam = sysUserLoginParam.getCode();
        logger.info("Session ID: {}, 前端传入的code: {}", request.getSession().getId(), codeParam);
        if(StringUtils.isEmpty(code)){
            return ResultVo.error("验证码过期!");
        }
        //对比验证码
        if(!codeParam.equals(code)){
            return ResultVo.error("验证码错误!");
        }
        //验证用户信息
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.lambda().eq(SysUser::getUsername, sysUserLoginParam.getUsername())
                .eq(SysUser::getPassword, sysUserLoginParam.getPassword());
//        DigestUtils.md5DigestAsHex(loginParam.getPassword().getBytes())
        SysUser user = sysUserService.getOne(query);
        if(user == null){
            return ResultVo.error("用户名或者密码错误!");
        }
        if(user.getStatus().equals("0")){ //0 停用 1启用
            return ResultVo.error("账户被停用，请联系管理员!");
        }
        //返回登录信息
        LoginVo vo = new LoginVo();
        vo.setUserid(user.getUserid());
        vo.setUsername(user.getUsername());
        return ResultVo.success("登录成功",vo);
    }
}
