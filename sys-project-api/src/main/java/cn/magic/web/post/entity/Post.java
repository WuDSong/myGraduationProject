package cn.magic.web.post.entity;

import cn.magic.web.topic.entity.Topic;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.beans.Transient;
import java.util.Date;
import java.util.List;

@Data
@TableName("post")
public class Post {
    @TableId(type = IdType.AUTO)
    private Long postId; //id
    private String title;   //标题
//    @TableField(typeHandler = JacksonTypeHandler.class) // 如果是本身是String 会导致字符串被二次序列化为 JSON 字符串
    private String content; //内容
    private Integer authorId;//作者id
    private Integer boardId; //板块
    private Date createdAt;
    private Date updatedAt;
    private Integer viewCount;//观看数目
    private Integer likeCount;//喜欢数量
    private String status;    //状态
    // 使用 JacksonTypeHandler 处理 JSON 字段
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> coverImages;
    private Boolean hasImages = false;

    @TableField(exist = false)  // 标记为可不存在，上传时候用
    private List<Integer> topicIds;  // List<String> topicNames 或 List<Integer> topicIds;
    @TableField(exist = false)  // 查询时候用，用于存储关联的话题信息
    private List<Topic> topicList;
}

//怎么检测boardId topic的合法