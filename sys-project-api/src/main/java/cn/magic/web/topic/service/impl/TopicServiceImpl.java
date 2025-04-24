package cn.magic.web.topic.service.impl;

import cn.magic.web.topic.entity.Topic;
import cn.magic.web.topic.mapper.TopicMapper;
import cn.magic.web.topic.service.TopicService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {
    @Override
    public List<Topic> getTopicsByPostId(Long postId) {
//        性能优化，直接用sql
        return this.baseMapper.getTopicsByPostId(postId);
    }
}
