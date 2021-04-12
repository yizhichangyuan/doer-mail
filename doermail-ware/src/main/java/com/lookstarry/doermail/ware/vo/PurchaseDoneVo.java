package com.lookstarry.doermail.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @PackageName:com.lookstarry.doermail.ware.vo
 * @NAME:PurchaseDoneVo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/12 11:29
 */
@Data
public class PurchaseDoneVo {
    @NotNull(message="采购单id不能为空")
    private Long id;

    /**
     * 采购项
     */
    private List<PurchaseDoneItemVo> items;
}
