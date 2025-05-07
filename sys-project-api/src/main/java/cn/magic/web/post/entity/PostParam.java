package cn.magic.web.post.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class PostParam {
    private Long curPage;  // 当前页码
    private Long pageSize; // 每页条数
    @TableField(exist = false)
    private String title;  // 查找标题,若为空则不查找
    @TableField(exist = false)
    private Long boardId;  //查找同一个版区的时候用
}
