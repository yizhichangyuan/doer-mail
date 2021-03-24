package com.lookstarry.doermail.order.dao;

import com.lookstarry.doermail.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 *
 * @author yizhichangyuan
 * @email 695999620@qq.com
 * @date 2021-03-13 13:49:09
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {

}
