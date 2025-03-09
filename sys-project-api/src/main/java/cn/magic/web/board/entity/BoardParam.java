package cn.magic.web.board.entity;

import lombok.Data;

@Data
public class BoardParam {
    private Long curPage; /// 当前页码
    private Long pageSize; // 每页条数
    private String name;//若不空则返回查询结果
}
