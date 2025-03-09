package cn.magic.web.role.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("role")
public class Role {
    @TableId(type = IdType.AUTO)
    private Long rid;
    private String roleName;
    private String roleKey;//标识符
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;
}