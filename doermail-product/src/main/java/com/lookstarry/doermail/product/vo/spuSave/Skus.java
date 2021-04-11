/**
  * Copyright 2021 bejson.com 
  */
package com.lookstarry.doermail.product.vo.spuSave;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2021-04-10 17:54:13
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Skus {
    /**
     * 商品销售信息
     */
    private List<Attr> attr;
    private String skuName;
    /**
     * 商品最终价格
     */
    private BigDecimal price;
    /**
     * 主标题，根据最终用户选择的版本和内存不同显示而不同
     */
    private String skuTitle;
    /**
     * 副标题
     */
    private String skuSubtitle;
    /**
     * 该SKu的商品展示图集，例如：不同颜色的手机商品展示图集是不同的
     */
    private List<Images> images;
    /**
     * 商品销售属性的笛卡尔积，就是这里的attr集合
     */
    private List<String> descar;


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


    /**
     * 不同会员等级的价格
     */
    private List<MemberPrice> memberPrice;

}