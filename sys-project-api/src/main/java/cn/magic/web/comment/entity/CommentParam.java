package cn.magic.web.comment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
// 查找顶级菜单
@Data
public class CommentParam {
    // 用于分页查找
    private Long curPage;
    private Long pageSize;

    @TableField(exist = false)
    private Long postId;        //当前帖子的id,用来查找这条post的评论
    @TableField(exist = false)
    private Long commentId;     //当前评论的id,用来查找当前评论的子孙
    @TableField(exist = false)
    private Long authorId;      //当前评论的作者id，获取父评论的名字，展示回复谁

    @TableField(exist = false)
    private Long userId;        //当前登录用户id，用于检查是否喜欢这条评论
}
