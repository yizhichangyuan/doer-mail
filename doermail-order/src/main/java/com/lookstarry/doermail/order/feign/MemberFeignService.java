package com.lookstarry.doermail.order.feign;

import com.lookstarry.doermail.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @PackageName:com.lookstarry.doermail.order.feign
 * @NAME:MemberFeignSerivce
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/20 18:50
 */
@FeignClient("doermail-member")
public interface MemberFeignService {
    @GetMapping("/member/memberreceiveaddress/{memberId}/addr")
    List<MemberAddressVo> getAddressById(@PathVariable("memberId") Long memberId);
}
