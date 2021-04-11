package com.lookstarry.common.constant;

/**
 * @PackageName:com.lookstarry.common.constant
 * @NAME:ProductConstant
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/9 14:18
 */
public class ProductConstant {
    public enum AttrEnum{
        ATTR_TYPE_SALE(0, "销售属性"), ATTR_TYPE_BASE(1, "基本属性");

        private int code;
        private String msg;
        private AttrEnum(int code, String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
