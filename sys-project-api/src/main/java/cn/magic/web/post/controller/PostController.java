package cn.magic.web.post.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.post.entity.Post;
import cn.magic.web.post.service.PostService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    private PostService postService;

    // 新增帖子
    @PostMapping
    public ResultVo addPost(@RequestBody Post posts) {
        if (postService.save(posts)) {
            return ResultVo.success("新增成功!");
        }
        return ResultVo.error("新增失败!");
    }

    // 删除帖子
    @DeleteMapping("/{postId}")
    public ResultVo deletePost(@PathVariable Integer postId) {
        if (postService.removeById(postId)) {
            return ResultVo.success("删除成功!");
        }
        return ResultVo.error("删除失败!");
    }

    // 修改帖子
    @PutMapping
    public ResultVo updatePost(@RequestBody Post post) {
        if (postService.updateById(post)) {
            return ResultVo.success("编辑成功!");
        }
        return ResultVo.error("编辑失败!");
    }

    // 根据ID查询单个帖子
    @GetMapping("/{postId}")
    public ResultVo getPostById(@PathVariable Integer postId) {
        Post post= postService.getById(postId);
        return ResultVo.success("查找成功",post);
    }

    // 查询所有帖子
    @GetMapping("/allList")
    public ResultVo getAllPosts() {
        List <Post> list= postService.list();
        return ResultVo.success("查询成功",list);
    }

    // 分页查询帖子
    @GetMapping("/page")
    public Page<Post> getPostsByPage(@RequestParam(defaultValue = "1") long current,
                                      @RequestParam(defaultValue = "10") long size) {
        Page<Post> page = new Page<>(current, size);
        return postService.page(page);
    }

    // 根据作者ID查询帖子
    @GetMapping("/author/{authorId}")
    public List<Post> getPostsByAuthorId(@PathVariable Integer authorId) {
        QueryWrapper<Post> wrapper = new QueryWrapper<>();
        wrapper.eq("author_id", authorId);
        return postService.list(wrapper);
    }
}
