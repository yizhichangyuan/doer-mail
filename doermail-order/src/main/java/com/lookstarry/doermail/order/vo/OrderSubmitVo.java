package com.lookstarry.doermail.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @PackageName:com.lookstarry.doermail.order.vo
 * @NAME:OrderSubmitVo
 * @Description: 封装订单提交数据
 * @author: yizhichangyuan
 * @date:2021/6/16 19:24
 */
@Data
public class OrderSubmitVo {
    private Long attrId;  // 收货地址id
    private Integer payType; // 支付方式
    // 客户端无需提交购买的商品，只需要去用户购物车再获取一遍即可
    // 优惠、发票信息

    private String orderToken; //防重令牌
    private BigDecimal payPrice; //应付价格，验价
    // 用户相关信息直接从session中获取购买用户信息
    private String note; // 订单备注
}
