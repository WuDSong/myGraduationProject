package cn.magic.web.collect.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class CollectParam {
    private Long curPage; /// 当前页码
    private Long pageSize; // 每页条数
    private String title;
    private String userId;
}
