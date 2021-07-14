package com.lookstarry.doermail.ware.feign;

import com.lookstarry.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @PackageName:com.lookstarry.doermail.ware.feign
 * @NAME:OrderFeignService
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/27 18:06
 */
@FeignClient("doermail-order")
public interface OrderFeignService {
    @GetMapping("/order/order/status/{orderSn}")
    R getOrderStatus(@PathVariable("orderSn") String orderSn);
}
