package com.lookstarry.doermail.order.web;

import com.alipay.api.AlipayApiException;
import com.lookstarry.doermail.order.config.AlipayTemplate;
import com.lookstarry.doermail.order.service.OrderService;
import com.lookstarry.doermail.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @PackageName:com.lookstarry.doermail.order.web
 * @NAME:payWebController
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/30 16:45
 */
@Controller
public class PayWebController {
    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OrderService orderService;

    /**
     * 1、将支付页让浏览器展示
     * 2、支付成功以后，需要跳转到用户的订单列表页面
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @GetMapping(value = "/payOrder", produces = "text/html") // GetMapping produces属性设置返回属性为一个页面内容
    @ResponseBody
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
        PayVo payVo = orderService.getOrderPay(orderSn);
        String result = alipayTemplate.pay(payVo);  // 返回是一个页面
//        System.out.println(result);
        return result;
    }
}
