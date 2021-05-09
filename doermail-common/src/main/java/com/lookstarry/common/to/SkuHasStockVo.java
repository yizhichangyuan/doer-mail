package com.lookstarry.common.to;

import lombok.Data;

/**
 * @PackageName:com.lookstarry.common.to
 * @NAME:SkuHasStockVo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/16 21:48
 */
@Data
public class SkuHasStockVo {
    private Long skuId;

    private Boolean hasStock;
}
