package com.lookstarry.doermail.coupon.service.impl;

import com.lookstarry.doermail.coupon.entity.SeckillSkuRelationEntity;
import com.lookstarry.doermail.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.common.utils.Query;

import com.lookstarry.doermail.coupon.dao.SeckillSessionDao;
import com.lookstarry.doermail.coupon.entity.SeckillSessionEntity;
import com.lookstarry.doermail.coupon.service.SeckillSessionService;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {
    @Autowired
    SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getLatest3DaySession() {
        List<SeckillSessionEntity> sessionList = this.baseMapper.getLatest3DaySession();

        if (sessionList != null && sessionList.size() > 0) {
            List<Long> collectIds = sessionList.stream().map(item -> item.getId()).collect(Collectors.toList());
            List<SeckillSkuRelationEntity> relationEntities = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>().in("promotion_session_id", collectIds));

            sessionList = sessionList.stream().map(session -> {
                Long sessionId = session.getId();
                List<SeckillSkuRelationEntity> sessionRelationSkus = relationEntities.stream().filter(item -> item.getPromotionSessionId().equals(sessionId)).collect(Collectors.toList());
                session.setRelationSkus(sessionRelationSkus);
                return session;
            }).collect(Collectors.toList());
        }

        return sessionList;
    }
}