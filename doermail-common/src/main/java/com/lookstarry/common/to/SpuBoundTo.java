package com.lookstarry.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @PackageName:com.lookstarry.common.to
 * @NAME:SpuBoundTo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/11 11:54
 */
@Data
public class SpuBoundTo {
    private Long spuId;

    /**
     * 金币增长值
     */
    private BigDecimal buyBounds;
    /**
     * 会员成长值
     */
    private BigDecimal growBounds;
}
