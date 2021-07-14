package com.lookstarry.doermail.ware.enume;

/**
 * @PackageName:com.lookstarry.doermail.ware.enume
 * @NAME:WareLockStatus
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/27 15:50
 */
public enum WareLockStatus {
    WARE_HAVE_LOCKED(1, "库存项锁定成功"),
    WARE_HAVE_UNLOCKED(2, "库存项解锁成功"),
    WARE_HAVE_REDUCT(3, "库存项已扣减");

    private Integer code;
    private String message;

    private WareLockStatus(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
