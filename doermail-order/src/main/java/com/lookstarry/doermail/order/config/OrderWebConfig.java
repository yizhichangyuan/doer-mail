package com.lookstarry.doermail.order.config;

import com.lookstarry.doermail.order.interceptor.LoginUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @PackageName:com.lookstarry.doermail.order.config
 * @NAME:OrderWebConfig
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/20 11:50
 */
@Configuration
public class OrderWebConfig implements WebMvcConfigurer {
    @Autowired
    LoginUserInterceptor loginUserInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**");
    }
}
