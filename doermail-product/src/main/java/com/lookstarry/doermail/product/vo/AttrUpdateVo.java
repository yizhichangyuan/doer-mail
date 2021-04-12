package com.lookstarry.doermail.product.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @PackageName:com.lookstarry.doermail.product.vo
 * @NAME:AttrUpdateVo
 * @Description: 接收前端传回来的需要修改的SPU的属性和属性值
 * @author: yizhichangyuan
 * @date:2021/4/12 20:18
 */
@Data
public class AttrUpdateVo {
    /**
     * {
     * 	"attrId": 7,
     * 	"attrName": "入网型号",
     * 	"attrValue": "LIO-AL00",
     * 	"quickShow": 1
     * }
     */

    /**
     * 属性id
     */
    @NotNull(message="attrId不能为空")
    private Long attrId;
    /**
     * 属性名
     */
    private String attrName;

    /**
     * 对应的属性值
     */
    private String attrValue;

    /**
     * 快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整
     */
    private Integer showDesc;
}
