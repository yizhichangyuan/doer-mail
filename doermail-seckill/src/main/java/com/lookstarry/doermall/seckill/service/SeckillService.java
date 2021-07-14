package com.lookstarry.doermall.seckill.service;

import com.lookstarry.doermall.seckill.to.SecKillSkuRedisTo;

import java.util.List;

/**
 * @PackageName:com.lookstarry.doermall.seckill.service
 * @NAME:SeckillService
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/7/2 23:52
 */
public interface SeckillService {
    void uploadSeckillSkuLatest3Days();

    List<SecKillSkuRedisTo> getCurrentSeckillSkus();

    SecKillSkuRedisTo getSkuSeckillInfo(Long skuId);

    String kill(String killId, String key, Integer num);
}
