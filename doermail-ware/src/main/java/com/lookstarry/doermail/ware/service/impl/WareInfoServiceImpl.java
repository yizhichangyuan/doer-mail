package com.lookstarry.doermail.ware.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.common.utils.Query;

import com.lookstarry.doermail.ware.dao.WareInfoDao;
import com.lookstarry.doermail.ware.entity.WareInfoEntity;
import com.lookstarry.doermail.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<WareInfoEntity>();
        String key = (String)params.get("key");
        if(StringUtils.isNotEmpty(key)){
            queryWrapper.and((w) ->{
                w.eq("id", key).or().like("name", key)
                .or().like("address", key).or().like("areacode", key);
            });
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                queryWrapper);
        return new PageUtils(page);
    }
}