package com.lookstarry.common.constant;

/**
 * 自定义错误状态码，前面两位为对应哪个微服务模块，后面三位为具体什么异常
 * 错误码列表：
 * 10：通用
 *     001：参数格式校验
 *     002：短信验证码请求频率太高
 *  11：商品
 *  12：订单
 *  13：购物车
 *  14：物流
 *  15：用户
 */
public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数格式校验失败"),
    SMS_CODE_EXCEPTION(10002, "验证码获取频率太高，请稍后再试"),
    TOO_MANY_REQUEST(10003, "太多请求，请稍后重试"),
    PRODUCT_UP_EXCEPTION(11000, "商品上架异常"),
    USERNAME_EXIST_EXCEPTION(15001, "用户名已存在"),
    PHONE_EXIST_EXCEPTION(15002, "手机号已注册"),
    LOGIN_FAIL_EXCEPTION(15003, "用户名或密码错误"),
    NO_STOCK_EXCEPTION(21000, "商品库存不足");

    private int code;
    private String message;

    private BizCodeEnum(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
