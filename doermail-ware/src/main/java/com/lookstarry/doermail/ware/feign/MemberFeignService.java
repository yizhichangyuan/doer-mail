package com.lookstarry.doermail.ware.feign;

import com.lookstarry.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @PackageName:com.lookstarry.doermail.ware.feign
 * @NAME:MemberFeignService
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/15 21:52
 */
@FeignClient("doermail-member")
public interface MemberFeignService {
    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    R addrInfo(@PathVariable("id") Long id);
}
