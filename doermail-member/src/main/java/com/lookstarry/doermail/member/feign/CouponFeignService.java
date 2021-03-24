package com.lookstarry.doermail.member.feign;

import com.lookstarry.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @PackageName:com.lookstarry.doermail.member.feign
 * @NAME:CouponFeignService
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/3/13 21:46
 */

/**
 * 这是一个声明式的远程调用，指明调用注册中心的那个服务的那个接口方法
 */
@FeignClient("doermail-coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/coupon/member/list")
    public R membercoupons();
}
