package com.lookstarry.doermail.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.lookstarry.doermail.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private  String app_id = "2021000117680586";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCCACwuvDv8hmxLdaDgY/qDWprJ8JRBtpVF/1VLcbDCozHK2EzkjXCQEjkumM6wYlOVq0yffhbERu3vtJMgVux3SNnu2dUJaExWJA5s0RMFSyupo7Pog5AISoYteoSUN1unm0PA6GPsM3IUcYlGOKpZjQ8UM84sPCIoGHy/xzkByBALqw7+Y6nslntsKZZ0Xzs4PR4S6mJYXW+YmztkzKvOgqT2Y+x0S0IvEofXOy6ARmKMhshXvdWGIMmb+CU7ggh23WwgbU1qb+rkIDkCiTupH5dokVbSLZramZHpoVXzksLUjC7YSE7hmaAVKkXw6KuVb6oCHLCnEL+woCMPJKcHAgMBAAECggEAL3rgqDEXt6Ng6i6ME2hwlokA/8BeZaHJ40beYC+l33C7PtrBOrht5DDDscEdvE4tOPzXf2kN56IARQwusCEPPIHfFt14cXcmak/A9P1ndQd/avXfP4zNjjmpx/J3eaq+TvjmRg6acCpaAB13ha4siYh7eWVyZ7WOxBYted3JdFyYHv9npCoKBS1PGLNUlT6pk5uDDvcdUS62/0mJfeH4zNvRbxXide4Fi9Nvkr6vo4aDI3matTvL2ksYStTIQ8WJZJQLUsCTV6T0YP5UmqY1qWVPgUw6rvtsPCghA/TT3mU5RXnSrXR/Gm14+wf4MTPaCuuJhRM9UoJTUePZNCuMAQKBgQC+T9WNHBeoQbtaQd9OZbMXLdxUArYI6TMXoVN9yhEmqb7w0wicnugMV8VeXVryIMFOZ1veU1etrSiwX+TET8fpO2Z01kBFz640HhHGCLgosIV8JdO1pBzBi3NsegXDhvSayNNcdcs//t32K6Tx15NbUa7wQMaQ3l6l3lzuHZQLawKBgQCu3y0NI9GsSRD80qY0HYaBntvBZSTRIbBqo8Q5awmm3gqRHMaW5PRe+fF9Qp8vAHsVEKru/f94YZ9CEdbwZsm5eh+F7wLeRqWFbTbyBjFpvBQFoOdtNbN3hFJTgoo0cXutv+GiDNmsKMJNEpSGI9Bm3bQvpwSk2y5GGNwSdk411QKBgF0I3ha7kv0YFSTMawQCR4ifTMRtBtxYVDfLD+vAap4CAWvtXppiPII8LioxbRJM3PMdKBYJnCU1L1z5o/YsIkhsV2vqiUUXf8vOn1W2UYDiun60Z0i2z6/2Ihj3lfW99bntTxY2J4RDbfK350yq3EiaJ/EfWXLcolxn1fo5z3O9AoGBAKIm6/19M1WQQyJMqjU1E55qEdgY07Tgile4F6IucLq8tgutJTCSiU7wcU73UeghiAAWfooyGIwl6ak4JZixl2hXp5dn877sv1sj1IlD2Nn0vpQhibcozouKtVQefcz3VfndLXo4dDdBbDpPek2QNBTOi+CL/LBpVVVGg/rSnN8pAoGAaAC19kTRcbJZtvDtBuWyVP2DXWlUhCgEOySJWvujKpxBK5KBe0BJm6/hnSmbfzg9VFe84sEQ1Od1XGgOBBJcpgRQ+EhdJmQ2tCCXbKGliWmv2nnfye6r8A3fi2r2ZKDaTvIsFqciYS3VjS5Qs0cfHC2pyNy0JeKyE98P9nQ5gOU=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhrYy+39VL1FEDqZd6+WE929Vi2Ow/eVJKjogfFXO3Syk5POSk+qYMx5ckNOQs0RiMo2f3bS5C4u5ceJYnEXfFGWmFgF17PopT85Qf85yx3uf1+0lFxURA9KXgpcZsUiAsA249LWz/OFQEwrxuayp7RiQe1m1sMQulnvPW8rN8k2pP8JnU1C5/ZJt/JvPf4bv/3Jc/iyilpgM9deMSxISO4fFNLRQZ5otlDo7Lftx07lWX/4FyrNpw3wYfqkWlYtuAWSI4UyDJ2mv+pE1QJYaef9XqI+wQC32a4U2UbxGVf4BJI3DX9ycIvcSxojWj6jiXzxmut058xxdoDIg1wx3UQIDAQAB";

    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private String notify_url = "http://u2oy9m9ab0.cdhttp.cn/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 同步通知，支付成功，一般跳转到成功页
    private String return_url = "http://member.doermall.com/orderList.html";

    // 签名方式
    private String sign_type = "RSA2";

    // 字符编码格式
    private String charset = "utf-8";

    // 订单超过多长时间未支付将不能再被支付
    private String timeout_express = "30m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\"" + timeout_express +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应：" + result);

        return result;
    }
}
