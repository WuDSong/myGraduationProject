package cn.magic.web.log.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserActiveLog {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private Long userId;
}
