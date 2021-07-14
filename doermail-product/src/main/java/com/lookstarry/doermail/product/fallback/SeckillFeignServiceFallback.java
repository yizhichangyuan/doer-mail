package com.lookstarry.doermail.product.fallback;

import com.lookstarry.common.constant.BizCodeEnum;
import com.lookstarry.common.utils.R;
import com.lookstarry.doermail.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @PackageName:com.lookstarry.doermail.product.fallback
 * @NAME:SeckillFeignServiceFallback
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/7/6 20:00
 */
@Slf4j
@Component
public class SeckillFeignServiceFallback implements SeckillFeignService {
    @Override
    public R getSkuSeckillInfo(Long skuId) {
        log.info("熔断方法getSkuSeckillInfo调用...");
        return R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(), BizCodeEnum.TOO_MANY_REQUEST.getMessage());
    }
}
