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
public class SpuSaveVo {

    /**
     * 商品名称，例如就是华为Mate40
     */
    private String spuName;
    /**
     * 商品描述
     */
    private String spuDescription;
    private Long catelogId;
    private Long brandId;
    private BigDecimal weight;
    /**
     * 是否上架
     */
    private int publishStatus;
    /**
     * 详情ppt大图图集
     */
    private List<String> decript;
    /**
     * 左侧商品前后左右展示
     */
    private List<String> images;
    /**
     * SPU购买会员成长值信息：金币增长值、会员成长值
     */
    private Bounds bounds;
    /**
     * 商品的基本属性
     */
    private List<BaseAttrs> baseAttrs;
    /**
     * 该SPU下的所有SKU集合
     */
    private List<Skus> skus;
}