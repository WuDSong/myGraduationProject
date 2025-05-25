package cn.magic.web.report.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ReportCommentVo {
    private Long reportId;
    private String reason;        //举报 / 申请理由
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> images;  //提供图片
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;       //举报时间
    private Long commentId;
    private String currentContent;       //评论内容
}
