package com.lookstarry.doermail.order.enume;

/**
 * @PackageName:com.lookstarry.doermail.order.enume
 * @NAME:OrderCheckEnum
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/22 20:32
 */
public enum OrderCheckEnum {
    ORDER_CREATE_SUCCESS(0, "订单创建成功"),
    CHECK_SALE_FAIL(2, "订单验价错误"),
    ORDER_TOKEN_FAIL(1, "订单防重令牌验证失败"),
    STOCK_LOCK_FAIL(3, "锁定商品库存失败");

    private OrderCheckEnum(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

    private Integer code;
    private String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
