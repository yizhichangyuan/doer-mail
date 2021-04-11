package com.lookstarry.doermail.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.doermail.coupon.entity.SpuBoundsEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author yizhichangyuan
 * @email 695999620@qq.com
 * @date 2021-03-13 13:54:07
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBatchBoundsEntityList(List<SpuBoundsEntity> spuBoundsEntityList);

}

