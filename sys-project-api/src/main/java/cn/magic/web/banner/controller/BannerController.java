package cn.magic.web.banner.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.banner.entity.Banner;
import cn.magic.web.banner.entity.BannerParam;
import cn.magic.web.banner.service.BannerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/banner")
public class BannerController {
    @Autowired
    private BannerService bannerService;

    //新增
    @PostMapping
    public ResultVo add(@RequestBody Banner banner) {
        if (bannerService.save(banner)) {
            return ResultVo.success("新增成功!");
        }
        return ResultVo.error("新增失败!");
    }

    //编辑
    @PutMapping
    public ResultVo edit(@RequestBody Banner banner) {
        if (bannerService.updateById(banner)) {
            return ResultVo.success("编辑成功!");
        }
        return ResultVo.error("编辑失败!");
    }

    //删除
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id) {
        if (bannerService.removeById(id)) {
            return ResultVo.success("删除成功!");
        }
        return ResultVo.error("删除失败!");
    }

    //分页获取
    @GetMapping("/list")
    public ResultVo getList(BannerParam bannerParam){
        //构造分页对象
        IPage<Banner> page = new Page<>(bannerParam.getCurPage(),bannerParam.getPageSize());
        //构造查询条件
        QueryWrapper<Banner> query = new QueryWrapper<>();
        // 使用 Lambda 表达式构造查询条件
        if (StringUtils.isNotEmpty(bannerParam.getName())) { //如果查询的参数Username有值，则进行模糊查找
            query.lambda().like(Banner::getTitle, bannerParam.getName());
        }
        //查询
        IPage<Banner> list = bannerService.page(page, query);
        return ResultVo.success("查询成功", list);
    }

    //获取所有
    @GetMapping("/getAllList")
    public ResultVo getAll(){
        List<Banner> list = bannerService.list();
        return ResultVo.success("查询成功", list);
    }

    // 查找有效的banner,查询小程序首页轮播图数据,且排序
    @GetMapping("/getActiveBanner")
    public ResultVo getActiveBanner() {
        Date currentTime = new Date();
        QueryWrapper<Banner> query = new QueryWrapper<>();
        query.lambda()
                .eq(Banner::getIsActive, "1")
                .and(wrapper -> {
                    wrapper
                            .isNull(Banner::getStartTime)
                            .and(innerWrapper -> innerWrapper.isNull(Banner::getEndTime).or().gt(Banner::getEndTime, currentTime))
                            .or()
                            .isNotNull(Banner::getStartTime)
                            .lt(Banner::getStartTime, currentTime)
                            .and(innerWrapper -> innerWrapper.isNull(Banner::getEndTime).or().gt(Banner::getEndTime, currentTime));
                })
                .orderByAsc(Banner::getSortOrder);
        List<Banner> list = bannerService.list(query);
        return list.isEmpty()? ResultVo.error("没有找到有效的轮播图") : ResultVo.success("查询成功", list);
    }

    //判断是否被占用
    @GetMapping("/isOccupied/{bannerName}")
    public ResultVo isOccupied(@PathVariable("bannerName") String name){
        QueryWrapper<Banner> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Banner::getTitle, name);
        Banner banner = bannerService.getOne(wrapper);
        if (banner != null) {
            return ResultVo.success("被占用！重新填写！",true);
        }
        return ResultVo.success("没有被占用",false);
    }
}
