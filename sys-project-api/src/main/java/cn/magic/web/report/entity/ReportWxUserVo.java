package cn.magic.web.report.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class ReportWxUserVo {
    private String reason;        //举报 / 申请理由
    private List<String> images;  //提供图片
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;       //举报时间
}
