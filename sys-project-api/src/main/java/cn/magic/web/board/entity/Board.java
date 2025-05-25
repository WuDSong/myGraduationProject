package cn.magic.web.board.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName("board")
public class Board {
    @TableId(type = IdType.AUTO)
    private Long boardId;
    private String name;
    private String icon;
    private String description;
    private Long sortOrder;
    private String status;
    private Boolean isDeleted;
    private Long creatorId;
    private Long parentId;
    @TableField(exist = false)
    private List<Board> children;
}