package com.lookstarry.doermail.ware.vo;

import lombok.Data;

/**
 * @PackageName:com.lookstarry.doermail.product.vo
 * @NAME:PurchaseDoneItemVo
 * @Description: 采购单中某一个采购项采购状态
 * @author: yizhichangyuan
 * @date:2021/4/12 11:23
 */
@Data
public class PurchaseDoneItemVo {
    /**
     * 采购项id
     */
    private Long itemId;

    /**
     * 采购需求项状态
     */
    private Integer status;

    /**
     * 采购失败填写原因
     */
    private String reason;
}
