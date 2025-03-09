package cn.magic.utils;
import lombok.Data;

import java.util.List;
//分页工具类
@Data
public class PageBean<T> {
    private Long pageSize; //每页显示条数
    private Long curPage; //当前页
    private Long totalCount; //总条数
    private Long totalPage;//总页数
    private List<T> data;//数据
}
