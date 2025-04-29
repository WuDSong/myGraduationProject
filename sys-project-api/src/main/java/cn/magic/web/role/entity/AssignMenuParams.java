package cn.magic.web.role.entity;

import lombok.Data;

import java.util.List;

@Data
public class AssignMenuParams {
    private Long rid;
    private List<Integer> menuIds;
}
