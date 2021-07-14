package com.lookstarry.doermail.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.lookstarry.doermail.order.config.AlipayTemplate;
import com.lookstarry.doermail.order.service.OrderService;
import com.lookstarry.doermail.order.vo.PayAsyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @PackageName:com.lookstarry.doermail.order.listener
 * @NAME:OrderPayedListener
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/7/1 22:36
 */
@RestController
public class OrderPayedListener {
    @Autowired
    OrderService orderService;

    @Autowired
    AlipayTemplate alipayTemplate;

    @PostMapping("/payed/notify")
    public String handleAlipayed(PayAsyncVo asyncVo, HttpServletRequest request) throws UnsupportedEncodingException, AlipayApiException {
//        Map<String, String[]> parameterMap = request.getParameterMap();
//        Set<String> keys = parameterMap.keySet();
//        for (String key : keys) {
//            System.out.println("参数名：" + key + "参数值：" + request.getParameter(key));
//        }
//        System.out.println("支付宝通知到位了..." + parameterMap);
        // 只要收到了支付宝给我们的异步通知，告诉我们订单支付成功，那么就应该返回success，支付宝就再也不通知

        // 验签（验证支付订单号是否正确，防止别人篡改信息）
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
//            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.alipay_public_key, alipayTemplate.getCharset(), alipayTemplate.getSign_type()); //调用SDK验证签名
        if(signVerified){
            // 签名验证成功
            System.out.println("签名验证成功...");
            String result = orderService.handlePayResult(asyncVo);
            return result;
        }else{
            return "error";
        }
    }
}
