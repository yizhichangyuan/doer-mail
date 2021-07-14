package com.lookstarry.doermall.seckill.controller;

import com.lookstarry.common.utils.R;
import com.lookstarry.doermall.seckill.service.SeckillService;
import com.lookstarry.doermall.seckill.to.SecKillSkuRedisTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @PackageName:com.lookstarry.doermall.seckill.controller
 * @NAME:SeckillController
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/7/4 11:19
 */
@Slf4j
@Controller
public class SeckillController {
    @Autowired
    SeckillService seckillService;

    /**
     * 返回当前时间可以参与的秒杀商品信息
     * @return
     */
    @ResponseBody
    @GetMapping("/currentSeckillSkus")
    public R getCurrentSeckillSkus(){
        log.info("currentSeckillSkus请求正在执行...");
        List<SecKillSkuRedisTo> redisTo = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(redisTo);
    }

    @ResponseBody
    @GetMapping("/sku/seckill/{skuId}")
    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId) throws InterruptedException {
        Thread.sleep(300);
        SecKillSkuRedisTo redisTo = seckillService.getSkuSeckillInfo(skuId);
        return R.ok().setData(redisTo);
    }

    // TODO 上架秒杀商品的时候，每个数据都有一个过期时间
    // TODO 秒杀后续的流程，简化了收货地址信息
    @GetMapping("/kill")
    public String secKill(@RequestParam("killId")String killId,
                     @RequestParam("key")String key,
                     @RequestParam("num")Integer num,
                          Model model){
        // 1、拦截器判断是否登陆
        try{
            String orderSn = seckillService.kill(killId, key, num);
            model.addAttribute("orderSn", orderSn);
        }catch(Exception e){
            System.out.println("秒杀发生异常..." + e.getMessage());
        }
        return "success";
    }

}
