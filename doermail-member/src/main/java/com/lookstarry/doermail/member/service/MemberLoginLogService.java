package com.lookstarry.doermail.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.doermail.member.entity.MemberLoginLogEntity;

import java.util.Map;

/**
 * 会员登录记录
 *
 * @author yizhichangyuan
 * @email 695999620@qq.com
 * @date 2021-03-13 13:59:56
 */
public interface MemberLoginLogService extends IService<MemberLoginLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

