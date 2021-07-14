package com.lookstarry.doermail.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.lookstarry.common.utils.R;
import com.lookstarry.doermail.ware.feign.MemberFeignService;
import com.lookstarry.doermail.ware.vo.FareVo;
import com.lookstarry.doermail.ware.vo.MemberAddressVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    @Autowired
    MemberFeignService memberFeignService;

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

    /**
     * 根据用户地址计算运费
     * @param attrId
     * @return
     */
    @Override
    public FareVo getFare(Long attrId) {
        FareVo fareVo = new FareVo();
        R r = memberFeignService.addrInfo(attrId);
        if(r.getCode() == 0){
            MemberAddressVo addressVo = r.getData(new TypeReference<MemberAddressVo>(){});
            if(addressVo != null){
                // 简单直接以电话的最后一位作为运费，其实这里可以直接对接快递100
                String phone = addressVo.getPhone();
                String substring = phone.substring(phone.length() - 1, phone.length());
                fareVo.setFare(new BigDecimal(substring));
                fareVo.setAddress(addressVo);
                return fareVo;
            }
        }
        return null;
    }
}