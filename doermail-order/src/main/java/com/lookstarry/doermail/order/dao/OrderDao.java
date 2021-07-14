package com.lookstarry.doermail.order.dao;

import com.lookstarry.doermail.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 *
 * @author yizhichangyuan
 * @email 695999620@qq.com
 * @date 2021-03-13 13:49:09
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    Integer updateOrderStatus(@Param("orderSn") String orderSn, @Param("code") Integer code);

}
