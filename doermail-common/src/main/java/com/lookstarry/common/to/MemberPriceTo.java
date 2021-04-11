package com.lookstarry.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @PackageName:com.lookstarry.common.to
 * @NAME:MemberPrice
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/11 12:31
 */
@Data
public class MemberPriceTo {
    private Long id;
    /**
     * 会员等级名称
     */
    private String name;
    /**
     * 会员价
     */
    private BigDecimal price;
}
