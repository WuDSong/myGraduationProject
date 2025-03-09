package cn.magic.web.sys_user.entity;

import lombok.Data;

@Data
public class SysUserParam {
    private Long curPage; /// 当前页码
    private Long pageSize; // 每页条数
    private String username;//若不空则返回查询结果
}
