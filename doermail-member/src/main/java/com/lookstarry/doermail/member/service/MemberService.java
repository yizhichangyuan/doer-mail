package com.lookstarry.doermail.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.doermail.member.entity.MemberEntity;
import com.lookstarry.doermail.member.exception.MobileExistException;
import com.lookstarry.doermail.member.exception.UsernameExistException;
import com.lookstarry.doermail.member.vo.MemberLoginVo;
import com.lookstarry.doermail.member.vo.MemberRegistVo;

import java.util.Map;

/**
 * 会员
 *
 * @author yizhichangyuan
 * @email 695999620@qq.com
 * @date 2021-03-13 13:59:56
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void registerMember(MemberRegistVo registVo) throws MobileExistException, UsernameExistException;

    void checkMobileUnique(String mobile) throws MobileExistException;

    void checkUsernameUnique(String userName) throws UsernameExistException;

    boolean loginMember(MemberLoginVo loginVo);

}

