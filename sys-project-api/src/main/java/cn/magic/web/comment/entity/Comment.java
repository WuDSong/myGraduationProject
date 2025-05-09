package cn.magic.web.comment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long commentId;     //评论id
    private Long postId;        //所属帖子
    private Long authorId;      //谁的
    private String content;
    private Long parentId;      //父评论id
    private Long rootId;        //根节点    和父亲一样
    private Long depth;         //深度 父亲+1
    private String path;        //路径 父亲+
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;
    private String likeCount;   //点赞
    private String status;

    // 回复
    @TableField(exist = false)
    List<Comment> children;  //目前只装一个孩子
    // 用户数据展示
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String avatarUrl;

    // 子评论还需要父评论的用户信息 用于评论区 我》父亲
    @TableField(exist = false)
    private String parentAuthorUsername;
    @TableField(exist = false)
    private String parentAuthorAvatarUrl;
    @TableField(exist = false)
    private String parentCommentContent;  // 父评论的内容 ，展示我的帖子的时候用

    @TableField(exist = false)
    private boolean liked =false;   //当前用户是否对其点赞,默认没有点赞
}
