package com.lookstarry.doermail.cart.vo;

import lombok.Data;

/**
 * @PackageName:com.doermall.cart.vo
 * @NAME:UserInfoVo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/16 17:34
 */
@Data
public class UserInfoVo {
    private Long userId; // 用户若登陆了的用户标识
    private String userKey; // 用户第一次访问购物车临时用户标识，以Cookie形式保存在浏览器端，主要用户用户未登录分配用户标识
    private boolean hasCookie = false;  // 浏览器端是否有用户标识Cookie user-key
}
