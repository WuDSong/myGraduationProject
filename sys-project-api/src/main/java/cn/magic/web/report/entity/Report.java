package cn.magic.web.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName(value = "report")
public class Report {
    @TableId(type = IdType.AUTO)
    private Long reportId;
    private String userId;
    private String targetType;  //类型
    private Long targetId;    //对应id
    private String reason;      //举报 / 申请理由
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;
    private String status;
    private String handledBy;  //处理人
    private String result;     //处理结果
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date handledAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
}
