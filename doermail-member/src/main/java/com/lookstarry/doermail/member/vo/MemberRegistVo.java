package com.lookstarry.doermail.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @PackageName:com.lookstarry.doermail.member.vo
 * @NAME:MemberRegistVo
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/7 20:33
 */
@Data
public class MemberRegistVo {
    private String userName;

    private String password;

    private String phone;
}
