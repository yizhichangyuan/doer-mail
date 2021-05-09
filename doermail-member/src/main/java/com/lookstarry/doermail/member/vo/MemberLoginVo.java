package com.lookstarry.doermail.member.vo;

import lombok.Data;

/**
 * @PackageName:com.lookstarry.doermail.member.vo
 * @NAME:MemberLoginVo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/8 17:37
 */
@Data
public class MemberLoginVo {
    /**
     * 可能是用户名，也可能是手机号，注册时已经保证了两个字段分别全局唯一
     */
    private String loginact;
    private String password;
}
