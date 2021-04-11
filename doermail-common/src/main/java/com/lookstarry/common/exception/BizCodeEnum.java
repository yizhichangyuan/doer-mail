package com.lookstarry.common.exception;

/**
 * 自定义错误状态码，前面两位为对应哪个微服务模块，后面三位为具体什么异常
 */
public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数格式校验失败");

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
