package cn.magic.web.board.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("board")
public class Board {
    @TableId(type = IdType.AUTO)
    private Long boardId;
    private String name;
    private String icon;
    private String description;
    private String sortOrder;
}