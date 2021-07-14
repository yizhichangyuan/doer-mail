package com.lookstarry.doermail.member.config;

import com.lookstarry.doermail.member.interceptor.LoginUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @PackageName:com.lookstarry.doermail.member.config
 * @NAME:MemberWebConfig
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/30 21:14
 */
public class MemberWebConfig implements WebMvcConfigurer {

    @Autowired
    LoginUserInterceptor loginUserInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**");
    }
}
