package com.lookstarry.doermall.seckill.constant;

import lombok.Data;

/**
 * @PackageName:com.lookstarry.doermall.seckill.constant
 * @NAME:SecKillRedisKey
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/7/3 14:47
 */
@Data
public class SecKillRedisKey {
    // 后面接活动id，为参与秒杀活动的所有商品列表的key，该秒杀活动所有商品列表放在一个hash结构中
    public static final String SECKILL_SKULIST_PRIFIX = "seckill:skuList:";

    public static final String SECKILL_SESSION_PRIFIX = "seckill:session:";

    // Redisson库存信号量的key，后面接的不是skuId，而是设置的随机码
    // 设置随机码的目的也是防止内部开发人员知道skuId后修改库存
    public static final String SECKILL_STOCK_SEMPHORE = "seckill:stock:";

    // 分布式情况下定时任务为保证只执行一次，利用分布式锁
    public static final String UPLOAD_LOCK = "seckill:upload:lock";

    public static final String HAVE_PARTIN_SECKILL = "seckill:have:participate";
}
