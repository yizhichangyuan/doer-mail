package com.lookstarry.doermail.order.feign;

import com.lookstarry.doermail.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @PackageName:com.lookstarry.doermail.order.feign
 * @NAME:CartFeignService
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/20 20:11
 */
@FeignClient("doermail-cart")
public interface CartFeignService {
    @GetMapping("/currentUserCartItems")
    List<OrderItemVo> currentUserCartItems();
}
