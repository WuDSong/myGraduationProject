package cn.magic.web.post.mapper;

import cn.magic.web.post.entity.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PostMapper extends BaseMapper<Post> {
    boolean insertPostTopic(@Param("postId")Long postId, @Param("topicIds") List<Integer> topicIds);

    boolean deletePostTopicByPostId(@Param("postId")Long postId);

    IPage<Post> getPostListWithUserInfo(Page<Post> page, @Param("title") String title);
}

