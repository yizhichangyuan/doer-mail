package com.lookstarry.doermail.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.lookstarry.common.constant.BizCodeEnum;
import com.lookstarry.doermail.member.exception.MobileExistException;
import com.lookstarry.doermail.member.exception.UsernameExistException;
import com.lookstarry.doermail.member.feign.CouponFeignService;
import com.lookstarry.doermail.member.vo.MemberLoginVo;
import com.lookstarry.doermail.member.vo.MemberRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.lookstarry.doermail.member.entity.MemberEntity;
import com.lookstarry.doermail.member.service.MemberService;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.common.utils.R;


/**
 * 会员
 *
 * @author yizhichangyuan
 * @email 695999620@qq.com
 * @date 2021-03-13 13:59:56
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeignService couponFeignService;

    @PostMapping("/register")
    public R memberRegist(@RequestBody MemberRegistVo registVo){
        MemberEntity memberEntity = null;
        try{
            memberEntity = memberService.registerMember(registVo);
        }catch(UsernameExistException e){
            return R.error(BizCodeEnum.USERNAME_EXIST_EXCEPTION.getCode(), BizCodeEnum.USERNAME_EXIST_EXCEPTION.getMessage());
        }catch(MobileExistException e){
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXIST_EXCEPTION.getMessage());
        }
        return R.ok().put("data", memberEntity);
    }

    @PostMapping("/login")
    public R memberLogin(@RequestBody MemberLoginVo loginVo){
        MemberEntity memberEntity = memberService.loginMember(loginVo);
        if(memberEntity == null){
            return R.error(BizCodeEnum.LOGIN_FAIL_EXCEPTION.getCode(), BizCodeEnum.LOGIN_FAIL_EXCEPTION.getMessage());
        }
        return R.ok().put("data", memberEntity);
    }

    @RequestMapping("/coupons")
    public R test() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");

        R membercoupons = couponFeignService.membercoupons();
        return R.ok().put("member", memberEntity).put("coupons", membercoupons.get("coupons"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
