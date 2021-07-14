package com.lookstarry.doermail.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @PackageName:com.doermall.cart.config
 * @NAME:DoermallFeignConfig
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/20 23:45
 */
@Configuration
public class DoermallFeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//                System.out.println("RequestInterceptor线程..." + Thread.currentThread().getId());
                if (requestAttributes != null) {
                    HttpServletRequest request = requestAttributes.getRequest();  // 老请求：浏览器发出的请求
                    // 同步请求头数据，Cookie
                    String cookie = request.getHeader("Cookie");
                    requestTemplate.header("Cookie", cookie);
                }
            }
        };
    }
}
