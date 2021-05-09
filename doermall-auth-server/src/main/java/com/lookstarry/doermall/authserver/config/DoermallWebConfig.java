package com.lookstarry.doermall.authserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @PackageName:com.lookstarry.doermall.authserver.config
 * @NAME:DoermallWebConfig
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/7 10:00
 */
@Configuration
public class DoermallWebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/register.html").setViewName("register");
    }
}
