package cn.magic.web.post.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BasePostInfoVo {   //用户查看自己的草稿返回值  当然也可以用于其他
    private Long postId; //id
    private String title;   //标题
    private String contentText; //文字内容
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
    private List<String> coverImages;
    @TableField(exist = false)
    private String content;
}
