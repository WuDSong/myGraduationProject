package cn.magic.web.post.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BasePostInfoVo {
    private Long postId; //id
    private String title;   //标题
    private String contentText; //文字内容
    private Date createdAt;
    private List<String> coverImages;
}
