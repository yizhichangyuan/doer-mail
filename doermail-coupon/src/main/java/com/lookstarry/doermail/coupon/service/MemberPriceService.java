package com.lookstarry.doermail.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.doermail.coupon.entity.MemberPriceEntity;

import java.util.Map;

/**
 * 商品会员价格
 *
 * @author yizhichangyuan
 * @email 695999620@qq.com
 * @date 2021-03-13 13:54:07
 */
public interface MemberPriceService extends IService<MemberPriceEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

