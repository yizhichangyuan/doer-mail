//package com.lookstarry.doermail.gateway.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.context.request.ServletWebRequest;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.context.request.WebRequestInterceptor;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.ServletRequest;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * @PackageName:com.lookstarry.doermail.gateway.config
// * @NAME:InterceptorConfig
// * @Description:
// * @author: yizhichangyuan
// * @date:2021/6/15 10:10
// */
//@Configuration
//public class InterceptorConfig implements HandlerInterceptor {
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        System.out.println(request.getRequestURL());
//        System.out.println("url正在访问...");
//        return true;
//    }
//}
