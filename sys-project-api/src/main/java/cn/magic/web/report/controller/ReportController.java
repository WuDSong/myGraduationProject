package cn.magic.web.report.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.report.entity.Report;
import cn.magic.web.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    //新增
    @PostMapping
    public ResultVo add(@RequestBody Report report) {
        if(report.getTargetType().equals("app")){
            report.setTargetId(0L);
        }
        if (reportService.save(report)) {
            return ResultVo.success("新增成功!");
        }
        return ResultVo.error("新增失败!");
    }

    //编辑
    @PutMapping
    public ResultVo edit(@RequestBody Report report) {
        if (reportService.updateById(report)) {
            return ResultVo.success("编辑成功!");
        }
        return ResultVo.error("编辑失败!");
    }

    //删除
    @Transactional
    @DeleteMapping("/{id}")
    public ResultVo delete(@PathVariable("id") Long id) {
        if (reportService.removeById(id)) {
            return ResultVo.success("删除成功!");
        }
        return ResultVo.error("删除失败!");
    }
}
