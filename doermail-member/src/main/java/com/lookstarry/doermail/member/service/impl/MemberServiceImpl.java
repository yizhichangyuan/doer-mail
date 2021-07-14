package com.lookstarry.doermail.member.service.impl;

import com.lookstarry.doermail.member.constant.MemberStatusConstant;
import com.lookstarry.doermail.member.entity.MemberLevelEntity;
import com.lookstarry.doermail.member.entity.MemberReceiveAddressEntity;
import com.lookstarry.doermail.member.exception.MobileExistException;
import com.lookstarry.doermail.member.exception.UsernameExistException;
import com.lookstarry.doermail.member.service.MemberLevelService;
import com.lookstarry.doermail.member.vo.MemberLoginVo;
import com.lookstarry.doermail.member.vo.MemberRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.common.utils.Query;

import com.lookstarry.doermail.member.dao.MemberDao;
import com.lookstarry.doermail.member.entity.MemberEntity;
import com.lookstarry.doermail.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    private MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 会员入库，需要检测手机号以及用户名是否存在，如果存在就抛出异常被controller感知，发给远程调用错误信息
     * @param registVo
     * @return
     */
    @Override
    public MemberEntity registerMember(MemberRegistVo registVo) throws MobileExistException, UsernameExistException{
        // 1、首先检查该手机号是否已经注册过
        checkMobileUnique(registVo.getPhone());
        checkUsernameUnique(registVo.getUserName());
        // 2、未注册过，给一些字段添加默认值
        MemberEntity memberEntity = getMemberEntity(registVo);
        this.save(memberEntity);
        return memberEntity;
    }

    @Override
    public void checkMobileUnique(String mobile) throws MobileExistException {
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("mobile", mobile));
        if(memberEntity != null){
            throw new MobileExistException("手机号已注册");
        }
    }

    @Override
    public void checkUsernameUnique(String userName) throws UsernameExistException{
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("username", userName));
        if(memberEntity != null){
            throw new UsernameExistException("用户名已存在");
        }
    }

    @Override
    public MemberEntity loginMember(MemberLoginVo loginVo) {
        // 同一个字符串MD5加密后不同，因为密码加密时盐值是随机的，所以只能按照mobile查找，然后比对
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("mobile", loginVo.getLoginact()).or().eq("username", loginVo.getLoginact()));
        if(memberEntity != null){
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean rightPassword = encoder.matches(loginVo.getPassword(), memberEntity.getPassword());
            if(rightPassword){
                return memberEntity;
            }
        }
        return null;
    }


    private MemberEntity getMemberEntity(MemberRegistVo registVo){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setMobile(registVo.getPhone());
        memberEntity.setUsername(registVo.getUserName());
        memberEntity.setCreateTime(new Date());
        memberEntity.setStatus(MemberStatusConstant.White_Status);

        // 密码需要加密后入库
        String encodePassword = encryptPassword(registVo.getPassword());
        memberEntity.setPassword(encodePassword);

        // 查询默认会员等级
        MemberLevelEntity defaultMemberLevel = memberLevelService.getOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));
        memberEntity.setLevelId(defaultMemberLevel.getId());
        return memberEntity;
    }

    /**
     * MD5盐值密码加密
     * @param password
     * @return
     */
    private String encryptPassword(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodePassword = encoder.encode(password);
        return encodePassword;
    }

}