package com.lookstarry.doermail.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.doermail.ware.entity.WareInfoEntity;
import com.lookstarry.doermail.ware.vo.FareVo;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author yizhichangyuan
 * @email 695999620@qq.com
 * @date 2021-03-13 14:03:12
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPageByCondition(Map<String, Object> params);

    FareVo getFare(Long attrId);

}

