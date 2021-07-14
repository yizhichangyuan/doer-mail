package com.lookstarry.common.to.mq;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @PackageName:com.lookstarry.common.to.mq
 * @NAME:QuickOrderTo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/7/5 14:15
 */
@Data
public class SeckillOrderTo {
    /**
     * 订单号
     */
    private String orderSn;

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
     * 秒杀数量
     */
    private Integer num;

    /**
     * 会员id
     */
    private Long memberId;

}
