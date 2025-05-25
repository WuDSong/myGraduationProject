package cn.magic.web.report.entity;

import cn.magic.web.post.entity.BasePostInfoVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
public class ReportPostVo {
    private Long reportId;
    private String reason;        //举报 / 申请理由\
    private List<String> images;  //提供图片
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;       //举报时间
    private Long postId;
    private BasePostInfoVo basePostInfoVo;
}
