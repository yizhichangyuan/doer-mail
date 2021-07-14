package com.lookstarry.doermail.order.feign;

import com.lookstarry.common.utils.R;
import com.lookstarry.doermail.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @PackageName:com.lookstarry.doermail.order.feign
 * @NAME:WareFeignService
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/15 20:46
 */
@FeignClient("doermail-ware")
public interface WareFeignService {
    @PostMapping("/ware/waresku/hasstock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);

    @GetMapping("/ware/wareinfo/fare")
    R getFare(@RequestParam("addrId") Long attrId);

    @PostMapping("/ware/waresku/lock/order")
    R orderLockStock(@RequestBody WareSkuLockVo vo);
}
