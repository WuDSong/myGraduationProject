package cn.magic.web.topic.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.topic.entity.Topic;
import cn.magic.web.topic.entity.TopicParam;
import cn.magic.web.topic.service.TopicService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topic")
public class TopicController {
    @Autowired
    private TopicService topicService;

    //新增
    @PostMapping
    public ResultVo add(@RequestBody Topic topic) {
        //当字段值为空字符串时，MyBatis-Plus会将其作为有效值插入，覆盖数据库默认值。需确保未传递的字段保持为 null 而非空字符串。
        if(topic.getTopicIcon().equals(""))
            topic.setTopicIcon(null);
        if(topic.getTopicDescription().equals(""))
            topic.setTopicDescription(null);
        if (topicService.save(topic)) {
            return ResultVo.success("新增成功!");
        }
        return ResultVo.error("新增失败!");
    }

    //编辑
    @PutMapping
    public ResultVo edit(@RequestBody Topic topic) {
        if (topicService.updateById(topic)) {
            return ResultVo.success("编辑成功!");
        }
        return ResultVo.error("编辑失败!");
    }

    //删除
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id) {
        Topic topic=topicService.getById(id);
        if(topic.getUsageCount()!=0)
            return ResultVo.error("删除失败!该话题已经被引用，只有不被引用，才可以删除！");
        if (topicService.removeById(id)) {
            return ResultVo.success("删除成功!");
        }
        return ResultVo.error("删除失败!");
    }

    //分页获取
    @GetMapping("/list")
    public ResultVo getList(TopicParam param){
        //构造分页对象
        IPage<Topic> page = new Page<>(param.getCurPage(), param.getPageSize());
        //构造查询条件
        QueryWrapper<Topic> query = new QueryWrapper<>();
        // 使用 Lambda 表达式构造查询条件
        if (StringUtils.isNotEmpty(param.getName())) { //如果查询的参数Username有值，则进行模糊查找
            query.lambda().like(Topic::getTopicName, param.getName());
        }
        //查询
        IPage<Topic> list = topicService.page(page, query);
        return ResultVo.success("查询成功", list);
    }
    //获取所有话题
    @GetMapping("/getAllList")
    public ResultVo getAll(){
        List<Topic> list = topicService.list();
        return ResultVo.success("查询成功", list);
    }

    //判断是否被占用,是否存在
    @GetMapping("/isOccupied/{topicName}")
    public ResultVo isOccupied(@PathVariable("topicName") String name){
        if (StringUtils.isBlank(name)) {
            return ResultVo.error("话题名称不能为空");
        }
//        QueryWrapper<Topic> wrapper = new QueryWrapper<>();
//        wrapper.lambda().eq(Topic::getTopicName, name);
//        Topic topic = topicService.getOne(wrapper);
//        if (topic != null) {
//            return ResultVo.success("被占用！重新填写！",true);
//        }
//        return ResultVo.success("没有被占用",false);

//        更高效的方式：使用 exists() 判断存在性（无需查询完整对象）
        boolean exists = topicService.lambdaQuery().eq(Topic::getTopicName, name).exists();
        if(exists){
            return ResultVo.success("被占用！重新填写！",true);
        }
        return ResultVo.success("没有被占用",false);
    }

    @GetMapping("/getTopicsByPostId/{postId}")
    public ResultVo getTopicsByPostId(@PathVariable("postId") Long postId){
        List <Topic> list =topicService.getTopicsByPostId(postId);
        if(list!=null&&list.size()>0){
            return ResultVo.success("获取话题成功",list);
        }
        return ResultVo.error("获取话题失败");
    }

}
