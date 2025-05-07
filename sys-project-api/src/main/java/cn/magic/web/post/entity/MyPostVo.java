package cn.magic.web.post.entity;

import lombok.Data;

import java.util.List;

@Data
public class MyPostVo {
    private List<Post> normal;
    private List<Post> pending_review;
    private List<Post> review_rejected;
}
