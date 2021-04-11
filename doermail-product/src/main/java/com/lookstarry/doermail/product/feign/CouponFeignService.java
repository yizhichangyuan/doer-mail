package com.lookstarry.doermail.product.feign;

import com.lookstarry.common.to.SkuReductionTo;
import com.lookstarry.common.to.SpuBoundTo;
import com.lookstarry.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @PackageName:com.lookstarry.doermail.product.feign
 * @NAME:CouponFeignService
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/10 22:22
 */
@FeignClient("doermail-coupon")
public interface CouponFeignService {

    /**
     * 1、CouponFeignService.saveSpuBound(SpuBoundTo)
     *   1)、@RequestBody会将这个对象转为JSON
     *   2）、在注册中心中找到doermail-coupon这个服务，给coupon/spubounds/save发送请求
     *      将上一步收到的JSON放在请求体位置，发送请求
     *   3)、对方服务收到请求后，请求体内有JSON数据
     *      （接收到标注了@RequestBody SpuBoundsEntity spuBounds），会将请求的JSON转为SpuBoundsEntity
     *   所以对应的JSON数据是兼容的，双方服务无需使用一个to，此外响应模型R也要是一样的
     * @param spuBoundTo
     */
    @PostMapping("coupon/spubounds/save")
    R saveSpuBound(@RequestBody SpuBoundTo spuBoundTo);


    @PostMapping("coupon/skufullreduction/saveBatchSkuReduction")
    R saveBatchSkuReduction(@RequestBody List<SkuReductionTo> skuReductionToList);
}
