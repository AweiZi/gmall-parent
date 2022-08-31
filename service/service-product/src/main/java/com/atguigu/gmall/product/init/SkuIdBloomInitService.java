package com.atguigu.gmall.product.init;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.product.service.SkuInfoService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Slf4j
public class SkuIdBloomInitService {
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    RedissonClient redissonClient;
    //TODO 布隆只能增加商品，不能上出商品，如果数据库库真的删除了商品，布隆怎么办
    //布隆重建

    /**
     * 项目一运行就启动
     */
    @PostConstruct
    public void initSkuBloom() {
        log.info("布隆初始化正在进行....");
        //1.查出所有的skuid
        List<Long> skuIds = skuInfoService.findAllSkuId();
        //2.把所有的id初始化到布隆过滤器
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);
        //3.初始化布隆过滤器
        boolean exists = filter.isExists();
        if (!exists) {
            //尝试初始化。如果布隆过滤器没有初始化过，就尝试初始化
            filter.tryInit(5000000, 0.00001);
        }
        //4.把所有的商品id添加到布隆过滤器中，不害怕某个微服务把这个事情做失败
        skuIds.forEach(skuId -> filter.add(skuId));
        log.info("布隆初始化完成....，总计添加了 {} 条数据", skuIds.size());
    }

}
