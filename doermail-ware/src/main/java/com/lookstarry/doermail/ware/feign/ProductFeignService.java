package com.lookstarry.doermail.ware.feign;

import com.lookstarry.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @PackageName:com.lookstarry.doermail.ware.feign
 * @NAME:ProductFeignService
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/12 15:32
 */
@FeignClient("doermail-product")
public interface ProductFeignService {
    /**
     * 服务之间feign相互调用有两种方式：
     * 1）让所有请求过网关
     * 给网关发请求，网关再路由到相应的服务
     * @PostMapping("/api/product/skuinfo/listByBatchId") ，此时为@FeignClient("doermail-gateway")
     * 2) 让请求直接发给相应服务
     * @PostMapping("/product/skuinfo/listByBatchId")，此时为@FeignClient("doermail-product")
     */
    @PostMapping("/product/skuinfo/listByBatchId")
    R listByBatchId(@RequestBody List<Long> skuIds);

    @RequestMapping("/product/spuinfo/info/{id}")
    R spuInfo(@PathVariable("id") Long id);
}
