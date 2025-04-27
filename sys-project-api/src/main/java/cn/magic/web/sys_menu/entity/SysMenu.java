package cn.magic.web.sys_menu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;
// 数据库映射实体/业务实体 目的：构建后端业务处理所需的完整菜单树（如权限管理、菜单编辑）
@Data
@TableName("sys_menu")
public class SysMenu {
    @TableId(type = IdType.AUTO)
    private Integer mid;
    private String menuName;
    private Integer menuType;
    private Integer parentId;
    private String path;
    private String icon;
    private Integer sort;
    private Integer visible = 1;
    private String perms;
    @TableField(exist = false)
    private List<SysMenu> children; // 用于前端树形结构
}
