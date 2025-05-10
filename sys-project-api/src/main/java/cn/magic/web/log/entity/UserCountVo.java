package cn.magic.web.log.entity;

import lombok.Data;

import java.util.List;

@Data
public class UserCountVo {
    private List<String> dates;
    private List<Long> activeCounts;
    private List<Long> registerCounts;
}
