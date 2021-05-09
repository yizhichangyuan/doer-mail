package com.lookstarry.doermail.member.exception;

/**
 * @PackageName:com.lookstarry.doermail.member.exception
 * @NAME:UsernameExistException
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/8 10:14
 */
public class UsernameExistException extends RuntimeException{
    public UsernameExistException(String msg) {
        super(msg);
    }
}
