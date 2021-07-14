package com.lookstarry.doermail.member.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.common.utils.R;
import com.lookstarry.doermail.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 1、spring-session依赖
 * 2、spring-session配置（序列化机制、session名称）
 * 3、引入LoginInterceptor、WebMvcConfigure
 * @PackageName:com.lookstarry.doermail.member.web
 * @NAME:MemberWebController
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/30 21:12
 */
@Controller
public class MemberWebController {
    @Autowired
    OrderFeignService orderFeignService;

    @RequestMapping("/orderList.html")
    public String memberOrderPage(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, Model model){
        // 支付成功也是返回该页面，可以通过此获取支付宝给我们传来的关于支付数据
        Map<String, Object> page = new HashMap<>();
        page.put("page", pageNum.toString());
        R r = orderFeignService.listWithItem(page);
        System.out.println(JSON.toJSONString(r));
        model.addAttribute("orders", r);
        return "orderList";
    }
}
