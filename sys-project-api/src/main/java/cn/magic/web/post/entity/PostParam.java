package cn.magic.web.post.entity;

import lombok.Data;

@Data
public class PostParam {
    private Long curPage;  // 当前页码
    private Long pageSize; // 每页条数
    private String title;  // 标题

}
