package cn.magic.web.post.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("post")
public class Post {
    @TableId("post_id")
    private Integer postId; //id
    private String title;   //标题
    private String content; //内容
    private Integer authorId;//作者id
    private Integer boardId; //板块
    private Date createdAt;
    private Date updatedAt;
    private Integer viewCount;//观看数目
    private Integer likeCount;//喜欢数量
    private String postCover; //封面
    private String status;    //状态
}