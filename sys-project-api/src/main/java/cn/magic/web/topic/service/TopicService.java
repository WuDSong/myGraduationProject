package cn.magic.web.topic.service;

import cn.magic.web.topic.entity.Topic;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface TopicService extends IService<Topic> {
    // 通过PostId查询话题
    List<Topic> getTopicsByPostId(Long postId);
}
