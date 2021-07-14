package com.lookstarry.doermail.order.vo;

import com.lookstarry.doermail.order.entity.OrderEntity;
import lombok.Data;

/**
 * @PackageName:com.lookstarry.doermail.order.vo
 * @NAME:SubmitOrderResponseVo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/16 20:12
 */
@Data
public class SubmitOrderResponseVo {
    private OrderEntity orderEntity; // 订单信息
    private Integer code; // 0成功 1失败 状态码

}
