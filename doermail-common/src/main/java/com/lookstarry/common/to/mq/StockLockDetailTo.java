package com.lookstarry.common.to.mq;

import lombok.Data;

/**
 * @PackageName:com.lookstarry.common.to
 * @NAME:StockLockDetailTo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/27 16:13
 */
@Data
public class StockLockDetailTo {
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;
    /**
     * 仓库id
     */
    private Long wareId;
    /**
     * 1-已锁定  2-已解锁  3-扣减
     */
    private Integer lockStatus;
}
