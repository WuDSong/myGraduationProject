package cn.magic.web.collect.service.impl;

import cn.magic.web.collect.entity.Collect;
import cn.magic.web.collect.mapper.CollectMapper;
import cn.magic.web.collect.service.CollectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CollectServiceImpl extends ServiceImpl<CollectMapper, Collect> implements CollectService {
}
