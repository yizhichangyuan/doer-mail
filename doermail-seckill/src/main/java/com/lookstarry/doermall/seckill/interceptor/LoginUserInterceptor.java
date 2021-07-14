package com.lookstarry.doermall.seckill.interceptor;

import com.lookstarry.common.constant.AuthServerConstant;
import com.lookstarry.common.to.MemberInfoEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @PackageName:com.lookstarry.doermall.seckill.interceptor
 * @NAME:LoginUserInterceptor
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/7/5 00:33
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {
    public static final ThreadLocal<MemberInfoEntity> LOGIN_USER_THREADLOCAL = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/kill", request.getRequestURI());
        if(match){
            MemberInfoEntity loginUser = (MemberInfoEntity)request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
            if(loginUser != null){
                LOGIN_USER_THREADLOCAL.set(loginUser);
                return true;
            }else{
                request.getSession().setAttribute("msg", "请先进行登陆");
                response.sendRedirect("http://auth.doermall.com/login.html");
                return false;
            }
        }
        return true;
    }
}
