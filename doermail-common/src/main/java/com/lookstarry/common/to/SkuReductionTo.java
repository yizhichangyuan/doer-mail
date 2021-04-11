package com.lookstarry.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @PackageName:com.lookstarry.common.to
 * @NAME:SkuReductionTo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/11 12:28
 */
@Data
public class SkuReductionTo {
    private Long skuId;

    /**
     * 买几件打几折，配合discount
     */
    private int fullCount;
    private BigDecimal discount;
    /**
     * 打折是否启用
     */
    private int countStatus;


    /**
     * 满多少减多少
     */
    private BigDecimal fullPrice;
    /**
     * 满减价格
     */
    private BigDecimal reducePrice;
    private int priceStatus;

    private List<MemberPriceTo> memberPrice;
}
