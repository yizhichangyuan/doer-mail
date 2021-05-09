package com.lookstarry.doermail.member.exception;

/**
 * @PackageName:com.lookstarry.doermail.member.exception
 * @NAME:MobileExistException
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/8 10:13
 */
public class MobileExistException extends RuntimeException{
    public MobileExistException(String msg) {
        super(msg);
    }
}
