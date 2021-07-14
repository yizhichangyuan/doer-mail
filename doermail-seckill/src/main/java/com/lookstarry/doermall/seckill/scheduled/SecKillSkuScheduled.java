package com.lookstarry.doermall.seckill.scheduled;

import com.lookstarry.doermall.seckill.constant.SecKillRedisKey;
import com.lookstarry.doermall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @PackageName:com.lookstarry.doermall.seckill.scheduled
 * @NAME:SecKillSkuScheduled
 * @Description: 秒杀商品的定时上架（每天晚上三点服务器空闲上架最近三天需要秒杀的商品）
 * @author: yizhichangyuan
 * @date:2021/7/2 22:40
 */
@Slf4j
@Service
public class SecKillSkuScheduled {
    @Autowired
    SeckillService seckillService;

    @Autowired
    RedissonClient redisson;

    @Scheduled(cron = "0 0 3 * * ?")
    public void uploadSeckillSkuLatest3Days(){
        // 秒杀商品重复上架无需处理
        System.out.println("上架秒杀商品信息...");
        RLock lock = redisson.getLock(SecKillRedisKey.UPLOAD_LOCK);
        try{
            lock.lock(10, TimeUnit.SECONDS); // 该锁只锁住十秒，此时任务应该完成
            seckillService.uploadSeckillSkuLatest3Days();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }finally {
            // 不管是否有异常，最后一定要解锁
            lock.unlock();
        }
    }
}
