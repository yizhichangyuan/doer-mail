package com.lookstarry.common.constant;

/**
 * @PackageName:com.lookstarry.common.constant
 * @NAME:CartConstant
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/16 17:36
 */
public class CartConstant {
    public static  final String TEMP_USER_COOKIE_NAME = "user-key";
    public static  final Integer TEMP_USER_COOKIE_TIMEOUT = 60 * 60 * 24 * 30; // cookie失效时间为30天
    public static final String CART_REDIS_KEY = "doermall:cart:";

}
