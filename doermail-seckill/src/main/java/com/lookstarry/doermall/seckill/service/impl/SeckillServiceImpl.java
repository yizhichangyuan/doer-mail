package com.lookstarry.doermall.seckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.lookstarry.common.to.MemberInfoEntity;
import com.lookstarry.common.to.mq.SeckillOrderTo;
import com.lookstarry.common.utils.R;
import com.lookstarry.doermall.seckill.constant.SecKillRedisKey;
import com.lookstarry.doermall.seckill.feign.CouponFeignService;
import com.lookstarry.doermall.seckill.feign.ProductFeignService;
import com.lookstarry.doermall.seckill.interceptor.LoginUserInterceptor;
import com.lookstarry.doermall.seckill.service.SeckillService;
import com.lookstarry.doermall.seckill.to.SecKillSkuRedisTo;
import com.lookstarry.doermall.seckill.vo.SecKillSessionWithSKusVo;
import com.lookstarry.doermall.seckill.vo.SeckillSkuRelationEntity;
import com.lookstarry.doermall.seckill.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @PackageName:com.lookstarry.doermall.seckill.service.impl
 * @NAME:SeckillServiceImpl
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/7/2 23:52
 */
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    RedissonClient redisson;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void uploadSeckillSkuLatest3Days() {
        R r = couponFeignService.getLatest3DaySession();
        if (r.getCode() == 0) {
            List<SecKillSessionWithSKusVo> sessionData = r.getData(new TypeReference<List<SecKillSessionWithSKusVo>>() {
            });
            // 上架：将所有的商品信息放到Redis中
            // 1、缓存活动信息
            saveSessionInfos(sessionData);
            // 2、缓存活动关联的所有商品信息
            saveSessionSkuInfos(sessionData);

        }
    }

    public List<SecKillSkuRedisTo> blockHandler(BlockException e) {
        log.error("资源getCurrentSeckillSkusResource被限流了..." + e.getMessage());
        return null;
    }


    @SentinelResource(value="getCurrentSeckillSkusResource", blockHandler = "blockHandler")
    @Override
    public List<SecKillSkuRedisTo> getCurrentSeckillSkus() {
        ArrayList<SecKillSkuRedisTo> secKillSkuRedisTos = new ArrayList<>();

        // 1、获取请求发生时刻属于哪个秒杀场次
        long time = new Date().getTime();
        try(Entry entry = SphU.entry("seckillSkusCode")){
            // 2、获取所有的场次的时间
            String sessionKey = SecKillRedisKey.SECKILL_SESSION_PRIFIX + "*";
            Set<String> keys = stringRedisTemplate.keys(sessionKey);
            for (String key : keys) {
                Boolean sessionInTime = timeInSession(time, key);
                if(sessionInTime){
                    // 当前活动时间确认在当前时间
                    Set<String> sessionIdWithSkuIdSet = stringRedisTemplate.opsForSet().members(key);
                    BoundHashOperations<String, String, String> seckillOps = getSeckillOperations();
                    List<String> values = seckillOps.multiGet(sessionIdWithSkuIdSet);
                    for (String value : values) {
                        SecKillSkuRedisTo secKillSkuRedisTo = JSON.parseObject((String) value, SecKillSkuRedisTo.class);
                        // 当前秒杀开始了，所以需要随机码数据
//                    secKillSkuRedisTo.setRandomCode(null);
                        secKillSkuRedisTos.add(secKillSkuRedisTo);
                    }
                }
            }
        }catch(BlockException e){
            log.error("资源被限流：{}", e.getMessage());
        }
        return secKillSkuRedisTos;
    }

    /**
     * 找出指定skuId是否参与活动，可能一个商品未来三天参与好几个秒杀活动，所以挑选出开始时间最近的一场活动对应的id
     * @param skuId
     * @return
     */
    @Override
    public SecKillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        BoundHashOperations<String, String, String> seckillOps = getSeckillOperations();
        Set<String> keys = seckillOps.keys();
        ArrayList<SecKillSkuRedisTo> skuIdList = new ArrayList<>();
        for (String key : keys) {
            // key为活动id-skuId，所以需要正则匹配出所有指定skuId的商品
            if(Pattern.matches("\\d-" + skuId, key)){
                String jsonString = seckillOps.get(key);
                SecKillSkuRedisTo secKillSkuRedisTo = JSON.parseObject(jsonString, SecKillSkuRedisTo.class);
                skuIdList.add(secKillSkuRedisTo);
            }
        }
        if(skuIdList.size() > 0){
            // 升序排序
            skuIdList.sort(new Comparator<SecKillSkuRedisTo>() {
                @Override
                public int compare(SecKillSkuRedisTo o1, SecKillSkuRedisTo o2) {
                    return o1.getStartTime().compareTo(o2.getStartTime().longValue());
                }
            });
            SecKillSkuRedisTo secKillSkuRedisTo = skuIdList.get(0); // 该商品参与的最近一个秒杀活动的信息

            // 判断当前时间是否为该活动秒杀时间，如果不是则需要掩藏随机码，防止恶意攻击
            Long currentTime = new Date().getTime();
            if(currentTime > secKillSkuRedisTo.getEndTime() || currentTime < secKillSkuRedisTo.getStartTime()){
                secKillSkuRedisTo.setRandomCode(null);
            }
            return secKillSkuRedisTo;
        }
        return null;
    }

    /**
     * 判断活动活动时间范围是否包括当前时间
     * @param currentTime
     * @param sessionKey
     * @return
     */
    private Boolean timeInSession(Long currentTime, String sessionKey){
        String[] timeDuration = sessionKey.replace(SecKillRedisKey.SECKILL_SESSION_PRIFIX, "").split("-");
        Long startTime = Long.valueOf(timeDuration[0]);
        Long endTime = Long.valueOf(timeDuration[1]);
        return currentTime <= endTime && currentTime >= startTime;
    }

    /**
     * redis缓存秒杀活动信息
     * @param sessionData
     */
    private void saveSessionInfos(List<SecKillSessionWithSKusVo> sessionData){
        for (SecKillSessionWithSKusVo session : sessionData) {
            // 获取时间的Long值，方便比较
            Long startTime = session.getStartTime().getTime();
            Long endTime = session.getEndTime().getTime();
            // key为活动的开始时间和结束时间
            String sessionKey = SecKillRedisKey.SECKILL_SESSION_PRIFIX + startTime + "-" + endTime;
            // value为参加活动的所有秒杀商品的场次id-商品id  {sessionId}-{skuId}
            List<String> collectSkuIds = session.getRelationSkus().stream().map(sku -> session.getId().toString() + "-" + sku.getSkuId().toString()).collect(Collectors.toList());
            // 缓存活动信息
            stringRedisTemplate.opsForSet().add(sessionKey, collectSkuIds.toArray(new String[]{}));
        }
    }

    private void saveSessionSkuInfos(List<SecKillSessionWithSKusVo> sessionData){
        // 查询所有指定skuId的基本信息
        List<Long> skuIds = new ArrayList<>();
        sessionData.stream().forEach(session -> {
            List<Long> allSessionSkuIds = session.getRelationSkus().stream().map(sku -> sku.getSkuId()).collect(Collectors.toList());
            skuIds.addAll(allSessionSkuIds);
        });
        Map<Long, SkuInfoVo> skuIdToSKuInfoMap = null;
        R r = productFeignService.listSkuInfoByBatchId(skuIds);
        if(r.getCode() == 0){
            List<SkuInfoVo> data = r.getData(new TypeReference<List<SkuInfoVo>>() {});
            skuIdToSKuInfoMap = data.stream().collect(Collectors.toMap(SkuInfoVo::getSkuId, skuInfoVo -> skuInfoVo));
        }else{
            throw new RuntimeException("远程调用失败");
        }

        for (SecKillSessionWithSKusVo session : sessionData) {
            List<SeckillSkuRelationEntity> relationSkus = session.getRelationSkus();
            for (SeckillSkuRelationEntity skuRelationEntity : relationSkus) {
                // 商品的基本信息key 为参加活动的所有秒杀商品的场次id-商品id  {sessionId}-{skuId}
                Long skuId = skuRelationEntity.getSkuId();
                Long sessionId = session.getId();
                BoundHashOperations<String, String, String> seckillOps = getSeckillOperations();
                String skuHashKey = sessionId + "-" + skuId;

                // 防止商品重复上架，先判断该活动秒杀商品列表是否有该商品存在
                if(!seckillOps.hasKey(skuHashKey)){
                    // 将缓存商品的信息以JSON做为value
                    SecKillSkuRedisTo skuRedisTo = new SecKillSkuRedisTo();

                    // 1、Sku的秒杀信息
                    BeanUtils.copyProperties(skuRelationEntity, skuRedisTo);

                    // 2、Sku的基本数据
                    SkuInfoVo skuInfoVo = skuIdToSKuInfoMap.get(skuId);
                    skuRedisTo.setSkuInfo(skuInfoVo);

                    // 3、设置上架商品的秒杀时间信息
                    skuRedisTo.setStartTime(session.getStartTime().getTime());
                    skuRedisTo.setEndTime(session.getEndTime().getTime());

                    // 4、设置随机码 秒杀请求 seckill?skuId=1 => seckill?skuId=1&key=fasdfasfdasga
                    // 随机码的目的在于秒杀时不仅需要验证skuId，还要验证随机码，该随机码目的是防止别人知道商品id后恶意发送请求攻击
                    String token = UUID.randomUUID().toString().replace("-", "");
                    skuRedisTo.setRandomCode(token);

                    // 5、商品库存扣减利用Redisson Semaphore，这样分布式大并发请求也可以进行限流
                    // 信号量名称是使用信号量而不是skuId，防止内部开发人员知晓skuId后恶意修改库存信息
                    String semaphoreKey = SecKillRedisKey.SECKILL_STOCK_SEMPHORE + token;
                    RSemaphore semaphore = redisson.getSemaphore(semaphoreKey);
                    // 信号量的值就为商品可以秒杀的库存量
                    semaphore.trySetPermits(skuRelationEntity.getSeckillCount().intValue());

                    String jsonString = JSON.toJSONString(skuRedisTo);
                    seckillOps.put(skuHashKey, jsonString);
                }
            }
        }
    }

    /**
     * 获取所有秒杀活动的所有商品信息集合的操作
     * @return
     */
    private BoundHashOperations<String, String, String> getSeckillOperations(){
        return stringRedisTemplate.boundHashOps(SecKillRedisKey.SECKILL_SKULIST_PRIFIX);
    }

    @Override
    public String kill(String killId, String key, Integer num){
        // killId正则判断
        if(!Pattern.matches("\\d-\\d", killId)){
            return null;
        }
        String[] split = killId.split("-");
        String sessionId = split[0];
        String skuId = split[1];


        // 1、根据killId（sessionId_skuId）获取到秒杀商品信息
        BoundHashOperations<String, String, String> seckillOps = getSeckillOperations();
        String jsonString = seckillOps.get(killId);


        // 2、判断是否有该秒杀商品
        if(StringUtils.isEmpty(jsonString)){
            return null;
        }

        // 3、是否在秒杀时间结束之前、是否超过秒杀数量限制、是否随机码正确
        SecKillSkuRedisTo killSku = JSON.parseObject(jsonString, SecKillSkuRedisTo.class);
        long currentTime = new Date().getTime();
        Boolean validFlag = currentTime <= killSku.getEndTime()
                && currentTime >= killSku.getStartTime()
                && num <= killSku.getSeckillLimit().intValue()
                && key.equals(killSku.getRandomCode());
        if(!validFlag){
            return null;
        }


        // 4、判断该人是否已经参与过该商品的秒杀活动，该人已经秒杀到该商品的数量 + num是否超过秒杀数量限制
        // 只要这个人该商品已经秒杀过了，就去redis中占一个位置，保证幂等性，限制每个用户只参与一次该商品的秒杀。 key为userId_sessionId_skuId value为已经秒杀得到的数量
        MemberInfoEntity user = LoginUserInterceptor.LOGIN_USER_THREADLOCAL.get();
        String userHaveKillSkuKey = SecKillRedisKey.HAVE_PARTIN_SECKILL + String.format("%d_%s_%s", user.getId(), sessionId, skuId);
        // 利用setIfAbsent方法，保证值限制该用户只参与一次该商品的秒杀
        // 该redis键的过期时间为该秒杀活动剩余时间
        long ttl = killSku.getEndTime() - currentTime;
        Boolean setSuccess = stringRedisTemplate.opsForValue().setIfAbsent(userHaveKillSkuKey, num.toString(), Duration.ofMillis(ttl));
        if(!setSuccess){
            // 占位未成功，说明该人已经买过
            return null;
        }

        // 4、扣减库存（信号量）
        RSemaphore semaphore = redisson.getSemaphore(SecKillRedisKey.SECKILL_STOCK_SEMPHORE + key);
        try{
            // 最多用100毫秒尝试获取信号量扣减
            Boolean lockSuccess = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
            if(!lockSuccess){
                return null;
            }

            // 5、扣减信号量成功后快速下单（发送创建订单的消息给订单服务，订单超时未支付则取消，同时还原信号量以及该人已经秒杀库存数量）
            SeckillOrderTo seckillOrderTo = new SeckillOrderTo();

            // 6、直接返回一个订单号
            String timeId = IdWorker.getTimeId();
            seckillOrderTo.setOrderSn(timeId);
            seckillOrderTo.setSkuId(Long.parseLong(skuId));
            seckillOrderTo.setMemberId(user.getId());
            seckillOrderTo.setNum(num);
            seckillOrderTo.setPromotionSessionId(Long.parseLong(sessionId));
            seckillOrderTo.setSeckillPrice(killSku.getSeckillPrice());
            rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", seckillOrderTo);

            System.out.println("返回了orderSn..." + timeId);
            return timeId;
        }catch (Exception e){
            return null;
        }
    }

}
