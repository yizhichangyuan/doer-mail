package com.lookstarry.doermall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * @PackageName:com.lookstarry.doermall.seckill.scheduled
 * @NAME:HelloSchedule
 * @Description:
 * 1、@EnableScheduling 开启定时任务
 * 2、@Scheduled默认不是整合Quartz任务的
 * 3、只要在方法上添加@Scheduled，属性cron中写入时间表达式
 * @author: yizhichangyuan
 * @date:2021/7/2 21:27
 */
@Slf4j
@Component
//@EnableAsync
//@EnableScheduling
public class HelloSchedule {
    /**
     *
     */
//    @Async
//    @Scheduled(cron = "* * * * * ?")
//    public void hello() throws InterruptedException {
//        log.info("hello...");
//        Thread.sleep(3000);
////        CompletableFuture.runAsync(() -> {
////            xxxService.hello();
////        }, executor);
//    }
}
