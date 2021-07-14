package com.lookstarry.doermail.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @PackageName:com.lookstarry.doermail.order.vo
 * @NAME:OrderItemVo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/20 12:59
 */
@Data
public class OrderItemVo {
    private Long skuId;
    private String title;
    private String defaultImage;
    private List<String> saleAttr;  // 销售属性
    private BigDecimal unitPrice;  // 单价
    private Integer count;
    private BigDecimal totalPrice; // 总价

    // todo 查询库存状态
    private boolean hasStock; // 是否有货
    private BigDecimal weight; // 商品重量
}
