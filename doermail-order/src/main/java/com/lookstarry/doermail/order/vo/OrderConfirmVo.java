package com.lookstarry.doermail.order.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @PackageName:com.lookstarry.doermall.authserver.vo
 * @NAME:OrderConfirmVo
 * @Description: 订单确认页需要的信息
 * @author: yizhichangyuan
 * @date:2021/5/20 12:44
 */
public class OrderConfirmVo {
    // 收货地址，ums_member_receive_address
    @Getter @Setter
    List<MemberAddressVo> address = new ArrayList<>();

    // 所有选中的购物项
    @Getter @Setter
    List<OrderItemVo> orderItems;

    // 发票记录...

    // 优惠券信息：用户积分
    @Getter @Setter
    Integer integration;

    /**
     * 订单防重复提交令牌，防止因为网络延迟用户重复点击，订单重复下达
     */
    @Getter @Setter
    String orderToken;

    // 订单总额
//    BigDecimal total;
    public BigDecimal getTotal(){
        BigDecimal totalPrice = new BigDecimal("0");
        if(orderItems != null && orderItems.size() != 0){
            for (OrderItemVo orderItem : orderItems) {
                BigDecimal multiply = orderItem.getUnitPrice().multiply(new BigDecimal(orderItem.getCount()));
                totalPrice = totalPrice.add(multiply);
            }
        }
        return totalPrice;
    }

    // 应付总额
//    BigDecimal payPrice;

    public BigDecimal getPayPrice() {
        return getTotal();
    }

    public Integer getCount(){
        return orderItems.size();
    }


    /**
     * sku信息
     *
     * @author yizhichangyuan
     * @email 695999620@qq.com
     * @date 2021-03-13 00:04:05
     */
    @Data
    @TableName("pms_sku_info")
    public static class SkuInfoEntity implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * skuId
         */
        @TableId
        private Long skuId;
        /**
         * spuId
         */
        private Long spuId;
        /**
         * sku名称
         */
        private String skuName;
        /**
         * sku介绍描述
         */
        private String skuDesc;
        /**
         * 所属分类id
         */
        private Long catelogId;
        /**
         * 品牌id
         */
        private Long brandId;
        /**
         * 默认图片
         */
        private String skuDefaultImg;
        /**
         * 标题
         */
        private String skuTitle;
        /**
         * 副标题
         */
        private String skuSubtitle;
        /**
         * 价格
         */
        private BigDecimal price;
        /**
         * 销量
         */
        private Long saleCount;

    }
}
