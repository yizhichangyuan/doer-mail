package com.lookstarry.doermail.authserver.feign;

import com.lookstarry.common.utils.R;
import com.lookstarry.doermail.authserver.vo.UserRegisterVo;
import com.lookstarry.doermail.authserver.vo.UserLoginVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @PackageName:com.lookstarry.doermall.authserver.feign
 * @NAME:MemberFeignService
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/8 12:13
 */
@FeignClient("doermail-member")
public interface MemberFeignService {
    @PostMapping("/member/member/register")
    R memberRegist(@RequestBody UserRegisterVo registVo);

    @PostMapping("/member/member/login")
    R memberLogin(@RequestBody UserLoginVo loginVo);
}
