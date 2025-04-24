package cn.magic.web.topic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("topic")
public class Topic {
    @TableId(value = "topic_id", type = IdType.AUTO)
    private Integer topicId;         // 话题ID
    @TableField("name")
    private String topicName;        // 话题名称（映射name字段）
    @TableField("description")       // 新增字段
    private String topicDescription; // 话题描述
    @TableField("icon")              // 新增字段 映射时value可以不写
    private String topicIcon;        // 话题图标URL
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt; // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt; // 最后更新时间
    @TableField("usage_count")
    private Integer usageCount = 0;  // 使用次数（默认值0）
    private String status;
    private Boolean isDeleted;
}
