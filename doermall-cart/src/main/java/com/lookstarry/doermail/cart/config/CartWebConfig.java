package com.lookstarry.doermail.cart.config;

import com.lookstarry.doermail.cart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @PackageName:com.doermall.cart.config
 * @NAME:CartWebConfig
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/16 18:45
 */
@Configuration
public class CartWebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
    }
}
