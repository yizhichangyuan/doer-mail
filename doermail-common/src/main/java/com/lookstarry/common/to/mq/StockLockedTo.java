package com.lookstarry.common.to.mq;

import lombok.Data;

/**
 * @PackageName:com.lookstarry.common.to
 * @NAME:StockLockedTo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/27 15:58
 */
@Data
public class StockLockedTo {
    private Long wareTaskId;  // 库存工作单id
    private StockLockDetailTo stockLockDetailTo; // 所有库存锁定项的id
}
