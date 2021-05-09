package com.lookstarry.doermail.thirdparty.controller;

import com.lookstarry.common.utils.R;
import com.lookstarry.doermail.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @PackageName:com.lookstarry.doermail.thirdparty.controller
 * @NAME:SMSController
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/7 12:19
 */
@RestController
@RequestMapping("/sms")
public class SMSController {
    @Autowired
    private SmsComponent smsComponent;

    @GetMapping("/sendcode")
    public R sendSMS(@RequestParam("phone") String phone, @RequestParam("code") String code){
        smsComponent.sendCode(phone, code);
        return R.ok();
    }
}
