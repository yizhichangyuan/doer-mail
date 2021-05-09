package com.lookstarry.doermail.product.vo;

import com.lookstarry.doermail.product.entity.SkuImagesEntity;
import com.lookstarry.doermail.product.entity.SkuInfoEntity;
import com.lookstarry.doermail.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @PackageName:com.lookstarry.doermail.product.vo
 * @NAME:SkuItemVo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/5 23:00
 */
@Data
public class SkuItemVo {
    // 1、sku 基本信息 pms_sku_info
    SkuInfoEntity info;

    boolean hasStock = true;

    // 2、sku的图片信息 pms_sku_images
    List<SkuImagesEntity> images;

    // 3、获取sku所属spu下所有sku的销售属性组合
    List<SkuItemSaleAttrVo> saleAttrs;

    // 4、获取spu的介绍
    SpuInfoDescEntity desp;

    // 5、获取spu的规格参数信息
    List<SpuItemAttrGroupVo> groupAttrs;


    @Data
    public static class SkuItemSaleAttrVo{
        private Long attrId;
        private String attrName;
        private List<AttrValueWithSkuIdVo> attrValues;
    }

    /**
     * 该attrValue可以对应的skuId集合，以逗号分隔
     */
    @Data
    public static class AttrValueWithSkuIdVo{
        private String attrValue;
        private String skuIds;
    }

    @Data
    public static class SpuItemAttrGroupVo{
        private String groupName;
        private List<SpuBaseAttrVo> attrs = new ArrayList<>();

    }

    @Data
    public static class SpuBaseAttrVo{
        private String attrName;
        private String attrValue;
    }
}
