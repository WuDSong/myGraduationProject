package cn.magic.web.post.entity;

import cn.magic.web.topic.entity.Topic;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.beans.Transient;
import java.util.Date;
import java.util.List;

@Data
@TableName(value = "post", autoResultMap = true)
public class Post {
    @TableId(type = IdType.AUTO)
    private Long postId; //id
    private String title;   //标题
//    @TableField(typeHandler = JacksonTypeHandler.class) // 如果是本身是String 会导致字符串被二次序列化为 JSON 字符串
    private String content; //内容
    private String contentText; //文字内容
    private Long authorId;//作者id
    private Integer boardId; //板块
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;
    private Integer viewCount;//观看数目
    private Integer likeCount;//喜欢数量
    private String status;    //状态
    // 使用 JacksonTypeHandler 处理 JSON 字段 ,类型处理器的作用是在 Java 对象和 JDBC 类型之间进行转换
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> coverImages;
    private Boolean hasImages = false;

    // 从这里开始下面都是表里没有的，其他东西

    // 话题处理
    @TableField(exist = false)  // 标记为可不存在
    private List<Integer> topicIds;  // List<String> topicNames 或 List<Integer> topicIds;上传时候用
    @TableField(exist = false)
    private List<Topic> topicList;   // 查询时候用，用于存储关联的话题信息

    // 小程序列表展示时还需要的内容
    @TableField(exist = false)
    private String avatarUrl;   //用户头像,小程序展示用户头像
    @TableField(exist = false)
    private String username;    //发布者用户名，上面那个只有用户名

}
