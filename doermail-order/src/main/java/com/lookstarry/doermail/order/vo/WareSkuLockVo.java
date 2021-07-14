package com.lookstarry.doermail.order.vo;

import lombok.Data;

import java.util.List;

/**
 * @PackageName:com.lookstarry.doermail.order.vo
 * @NAME:WareSkuLockVo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/22 20:49
 */
@Data
public class WareSkuLockVo {
    private String orderSn; // 订单号

    private List<OrderItemVo> locks; //需要锁住的所有库存信息
}
