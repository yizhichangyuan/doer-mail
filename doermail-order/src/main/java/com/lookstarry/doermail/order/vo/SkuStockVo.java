package com.lookstarry.doermail.order.vo;

import lombok.Data;

/**
 * @PackageName:com.lookstarry.doermail.order.vo
 * @NAME:SkuStockVo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/15 20:52
 */
@Data
public class SkuStockVo {
    private Long skuId;

    private Boolean hasStock;
}
