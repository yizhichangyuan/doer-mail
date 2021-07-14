package com.lookstarry.doermall.seckill.feign;

import com.lookstarry.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("doermail-product")
public interface ProductFeignService {
    @PostMapping("/product/skuinfo/listByBatchId")
    R listSkuInfoByBatchId(@RequestBody List<Long> skuIds);
}
