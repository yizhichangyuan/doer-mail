package com.lookstarry.doermail.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @PackageName:com.lookstarry.doermail.gateway.config
 * @NAME:CorsConfiguration
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/3/18 00:59
 */

/**
 * 对进入网关跨域请求之前发送的预检请求添加头部信息允许跨域
 */
@Configuration
public class DoerMailCorsConfiguration {
    // 这是一个filter，会在请求结束返回给浏览器时，会在响应头中加上这些告诉浏览器可以跨域
    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 1、配置跨域
        // 允许任何头部信息、方法、来源都可以跨域
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.setAllowCredentials(true); //允许携带cookie

        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }
}
