package com.lookstarry.doermall.seckill.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @PackageName:com.lookstarry.doermall.seckill.vo
 * @NAME:SkuInfoVo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/7/3 15:23
 */
@Data
public class SkuInfoVo {
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
