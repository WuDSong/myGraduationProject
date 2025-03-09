package cn.magic.web.sys_menu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
    private Integer visible;
    private String perms;
}
