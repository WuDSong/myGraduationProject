package cn.magic.web.topic.mapper;

import cn.magic.web.topic.entity.Topic;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TopicMapper extends BaseMapper<Topic> {
    List<Topic> getTopicsByPostId(@Param("postId") Long postId);
}
