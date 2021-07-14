package com.lookstarry.doermail.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @PackageName:com.lookstarry.doermall.seckill.to
 * @NAME:SecKillSkuRedisTo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/7/3 15:03
 */
@Data
public class SecKillSkuVo {
    private Long id;
    /**
     * 活动id
     */
    private Long promotionId;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private BigDecimal seckillCount;
    /**
     * 每人限购数量
     */
    private BigDecimal seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;

    // 商品秒杀的开始时间
    private Long startTime;

    // 商品秒杀的结束时间
    private Long endTime;

    // 随机码目的在于校验，防止别人通过页面知晓skuId后，秒杀利用软件进行恶意攻击
    private String randomCode;
}
