package cn.magic.web.banner.service.impl;

import cn.magic.web.banner.entity.Banner;
import cn.magic.web.banner.mapper.BannerMapper;
import cn.magic.web.banner.service.BannerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {
}
