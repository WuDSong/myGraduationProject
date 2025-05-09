package cn.magic.web.report.service.impl;

import cn.magic.web.report.entity.Report;
import cn.magic.web.report.mapper.ReportMapper;
import cn.magic.web.report.service.ReportService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {
}
