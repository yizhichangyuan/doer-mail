package com.lookstarry.doermail.cart.interceptor;

import com.lookstarry.doermail.cart.vo.UserInfoVo;
import com.lookstarry.common.constant.AuthServerConstant;
import com.lookstarry.common.constant.CartConstant;
import com.lookstarry.common.to.MemberInfoEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @PackageName:com.doermall.cart.interceptor
 * @NAME:CartInterceptor
 * @Description: 在执行目标方法之前，判断用户的登陆状态，并封装传递给controller目标请求
 * @author: yizhichangyuan
 * @date:2021/5/16 17:24
 */
public class CartInterceptor implements HandlerInterceptor {
    public static final ThreadLocal<UserInfoVo> THREAD_LOCAL = new ThreadLocal();
    /**
     * 在目标方法执行之前拦截
     * 用户登陆则从Session中拿userId
     * 如果用户未登陆，则分为第一次访问购物车还是已经访问过购物车
     * 判断是否第一次访问根据浏览器端cookie是否有分配的user-key
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoVo userInfoVo = new UserInfoVo();
        HttpSession session = request.getSession();
        MemberInfoEntity memberInfoEntity = (MemberInfoEntity)session.getAttribute(AuthServerConstant.LOGIN_USER);
        if(memberInfoEntity != null){
            // 用户登陆
            userInfoVo.setUserId(memberInfoEntity.getId());
        }

        // 用户未登陆，判断用户是否第一次访问购物车，即查看浏览器端是否存在cookie为user-key
        Cookie[] cookies = request.getCookies();
        if(cookies.length != 0){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                    userInfoVo.setUserKey(cookie.getValue());
                    userInfoVo.setHasCookie(true); // 浏览器端有cookie，则不再防止cookie
                    break;
                }
            }
        }

        // 用户第一次访问购物车，不管有无登陆，都分配一个临时用户给浏览器端保存，用户退出后添加信息仍然存在，在postHandle中放入浏览器端
        if(StringUtils.isEmpty(userInfoVo.getUserKey())){
            String uuid = UUID.randomUUID().toString();
            userInfoVo.setUserKey(uuid);
        }
        THREAD_LOCAL.set(userInfoVo);
        return true;
    }

    /**
     * 业务执行之后执行
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoVo userInfoVo = THREAD_LOCAL.get();
        // 如果浏览器端没有cookie，则在浏览器端放置一份
        if(!userInfoVo.isHasCookie()){
            Cookie cookie = new Cookie("user-key", userInfoVo.getUserKey());
            cookie.setDomain("doermall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }
}
