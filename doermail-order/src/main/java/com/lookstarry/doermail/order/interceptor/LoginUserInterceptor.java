package com.lookstarry.doermail.order.interceptor;

import com.lookstarry.common.constant.AuthServerConstant;
import com.lookstarry.common.to.MemberInfoEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @PackageName:com.lookstarry.doermail.order.interceptor
 * @NAME:LoginUserInterceptor
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/20 11:33
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberInfoEntity> threadLocal = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 对于仓储服务查询用户的订单状态，不需要进行服务认证，属于服务之间的调用，而不是用户发起的请求
        boolean match = new AntPathMatcher().match("/order/order/status/**", request.getRequestURI());
        boolean match1 = new AntPathMatcher().match("/payed/notify", request.getRequestURI());
        if(match || match1){
            return true;
        }
        HttpSession session = request.getSession();
        MemberInfoEntity loginUser = (MemberInfoEntity)session.getAttribute(AuthServerConstant.LOGIN_USER);
        if(loginUser != null){
            threadLocal.set(loginUser);
            return true;
        }
        // 未登录跳转到登陆页面
        request.getSession().setAttribute("msg", "请先进行登陆");
        response.sendRedirect("http://auth.doermall.com/login.html");
        return false;
    }
}
