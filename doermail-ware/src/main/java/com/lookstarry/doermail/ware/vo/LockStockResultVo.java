package com.lookstarry.doermail.ware.vo;

import lombok.Data;

/**
 * @PackageName:com.lookstarry.doermail.ware.vo
 * @NAME:LockStockResultVo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/22 20:59
 */
@Data
public class LockStockResultVo {
    private Long skuId;
    private Integer num;
    private Boolean locked;
}
