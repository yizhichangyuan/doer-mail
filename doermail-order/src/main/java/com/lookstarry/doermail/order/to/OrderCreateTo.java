package com.lookstarry.doermail.order.to;

import com.lookstarry.doermail.order.entity.OrderEntity;
import com.lookstarry.doermail.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @PackageName:com.lookstarry.doermail.order.to
 * @NAME:OrderCreateTo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/16 20:51
 */
@Data
public class OrderCreateTo {
    private OrderEntity order;
    private List<OrderItemEntity> items;
    private BigDecimal payPrice; // 订单计算应付价格
    private BigDecimal fare; // 运费
}
