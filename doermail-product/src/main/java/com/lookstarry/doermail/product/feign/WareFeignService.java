package com.lookstarry.doermail.product.feign;

import com.lookstarry.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @PackageName:com.lookstarry.doermail.product.feign
 * @NAME:WareFeignService
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/16 22:34
 */
@FeignClient("doermail-ware")
public interface WareFeignService {
    /**
     * 对于返回结果
     * 1、R设计的时候可以加上泛型T，返回接收的时候直接R<T>
     * 2、controller直接返回我们想要的结果
     * 3、自己在R中封装反序列化结果
     * @param skuIds
     * @return
     */
    @PostMapping("/ware/waresku/hasstock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);
}
