package com.lookstarry.doermail.coupon.service.impl;

import com.lookstarry.common.to.SkuReductionTo;
import com.lookstarry.doermail.coupon.entity.MemberPriceEntity;
import com.lookstarry.doermail.coupon.entity.SkuLadderEntity;
import com.lookstarry.doermail.coupon.service.MemberPriceService;
import com.lookstarry.doermail.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.common.utils.Query;

import com.lookstarry.doermail.coupon.dao.SkuFullReductionDao;
import com.lookstarry.doermail.coupon.entity.SkuFullReductionEntity;
import com.lookstarry.doermail.coupon.service.SkuFullReductionService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {
    @Autowired
    private MemberPriceService memberPriceService;

    @Autowired
    private SkuLadderService skuLadderService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveBatchSkuReduction(List<SkuReductionTo> skuReductionToList) {
        // 1.保存打几折信息 sms_sku_ladder
        // 2.保存满减价格 sms_sku_full_reduction
        // 3.保存会员价格 sms_member_price
        List<SkuLadderEntity> ladders = new ArrayList<>();
        List<SkuFullReductionEntity> fullReduces = new ArrayList<>();
        List<MemberPriceEntity> memberPrices = new ArrayList<>();

        extractLadderFullReductMemberPrice(skuReductionToList, ladders, fullReduces, memberPrices);
        skuLadderService.saveBatch(ladders);
        this.saveBatch(fullReduces);
        memberPriceService.saveBatch(memberPrices);
    }

    private void extractLadderFullReductMemberPrice(List<SkuReductionTo> skuReductionToList, List<SkuLadderEntity> ladders, List<SkuFullReductionEntity> fullReduces, List<MemberPriceEntity> memberPrices) {
        int size = skuReductionToList.size();
        for(int i = 0; i < size; i++){
            SkuReductionTo skuReduceTo = skuReductionToList.get(i);

            if(skuReduceTo.getFullCount() > 0){
                SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
                BeanUtils.copyProperties(skuReduceTo, skuLadderEntity);
                skuLadderEntity.setAddOther(skuReduceTo.getCountStatus());
                ladders.add(skuLadderEntity);
            }

            if(skuReduceTo.getFullPrice().compareTo(new BigDecimal(0)) > 0){
                SkuFullReductionEntity fullReduce = new SkuFullReductionEntity();
                BeanUtils.copyProperties(skuReduceTo, fullReduce);
                fullReduce.setAddOther(skuReduceTo.getPriceStatus());
                fullReduces.add(fullReduce);
            }


            List<MemberPriceEntity> memberPriceEntities = skuReduceTo.getMemberPrice().stream().filter((memberPrice) ->{
                return memberPrice.getPrice().compareTo(new BigDecimal(0)) > 0;
            }).map((memberPrice) ->{
                MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                memberPriceEntity.setMemberLevelId(memberPrice.getId());
                memberPriceEntity.setMemberPrice(memberPrice.getPrice());
                memberPriceEntity.setMemberLevelName(memberPrice.getName());
                memberPriceEntity.setAddOther(0);  // 默认不采用叠加优惠
                memberPriceEntity.setSkuId(skuReduceTo.getSkuId());
                return memberPriceEntity;
            }).collect(Collectors.toList());
            memberPrices.addAll(memberPriceEntities);
        }
    }

}