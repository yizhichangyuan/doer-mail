package com.lookstarry.doermail.authserver.feign;

import com.lookstarry.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @PackageName:com.lookstarry.doermall.authserver.feign
 * @NAME:AuthFeignService
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/7 12:24
 */
@FeignClient("doermail-third-party")
public interface AuthFeignService {
    @ResponseBody
    @GetMapping("/sms/sendcode")
    R sendSMS(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
