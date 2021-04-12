package com.lookstarry.doermail.ware.service.impl;

import com.lookstarry.doermail.ware.vo.PurchaseDoneItemVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.common.utils.Query;

import com.lookstarry.doermail.ware.dao.PurchaseDetailDao;
import com.lookstarry.doermail.ware.entity.PurchaseDetailEntity;
import com.lookstarry.doermail.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * key 查询关键词
         * status 需求状态：例如是否已完成
         * wareId：仓库Id
         */
        String key = (String)params.get("key");
        String status = (String)params.get("status");
        String wareId = (String)params.get("wareId");
        QueryWrapper<PurchaseDetailEntity> queryWrapper = compactQueryWrapper(key, status, wareId);
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    private QueryWrapper<PurchaseDetailEntity> compactQueryWrapper(String key, String status, String wareId) {
        QueryWrapper<PurchaseDetailEntity> queryWrapper = new QueryWrapper<PurchaseDetailEntity>();
        if(StringUtils.isNotEmpty(key)){
            queryWrapper.and((w) ->{
                w.eq("id", key).or().eq("purchase_id", key).
                        or().eq("sku_id", key);
            });
        }
        if(StringUtils.isNotEmpty(status) && StringUtils.isNumeric(status)){
            queryWrapper.eq("status", status);
        }
        if(StringUtils.isNotEmpty(wareId) && StringUtils.isNumeric(wareId)){
            queryWrapper.eq("ware_id", wareId);
        }
        return queryWrapper;
    }

}