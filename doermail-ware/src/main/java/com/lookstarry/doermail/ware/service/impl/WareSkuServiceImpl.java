package com.lookstarry.doermail.ware.service.impl;

import com.lookstarry.common.to.SkuHasStockVo;
import com.lookstarry.doermail.ware.entity.WareSkuLeftEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.common.utils.Query;

import com.lookstarry.doermail.ware.dao.WareSkuDao;
import com.lookstarry.doermail.ware.entity.WareSkuEntity;
import com.lookstarry.doermail.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<WareSkuEntity>();
        String wareId = (String)params.get("wareId");
        String skuId = (String)params.get("skuId");
        System.out.println("skuId:" + skuId);
        if(StringUtils.isNotEmpty(wareId) && StringUtils.isNumeric(wareId) && !wareId.equalsIgnoreCase("0")){
            queryWrapper.eq("ware_id", wareId);
        }
        if(StringUtils.isNotEmpty(skuId) && StringUtils.isNumeric(skuId) && !skuId.equalsIgnoreCase("0")){
            queryWrapper.eq("sku_id", skuId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuHasStockVo> hasStock(List<Long> skuIds) {
        if(skuIds != null && skuIds.size() != 0){
            List<WareSkuLeftEntity> wareSkuLeftEntities = this.baseMapper.selectLeftStock(skuIds);
            Map<Long, Long> idToStock = wareSkuLeftEntities.stream().collect(Collectors.toMap(WareSkuLeftEntity::getSkuId, WareSkuLeftEntity::getStockLeft));
            List<SkuHasStockVo> skuHasStockVos = skuIds.stream().map((id) -> {
                SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
                skuHasStockVo.setSkuId(id);
                if(idToStock != null){
                    skuHasStockVo.setHasStock(idToStock.get(id) == null ? false : idToStock.get(id) > 0);
                }else{
                    skuHasStockVo.setHasStock(false);
                }
                return skuHasStockVo;
            }).collect(Collectors.toList());
            return skuHasStockVos;
        }
        return null;
    }

}