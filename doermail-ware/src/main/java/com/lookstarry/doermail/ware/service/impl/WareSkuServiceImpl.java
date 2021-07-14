package com.lookstarry.doermail.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lookstarry.common.exception.NoStockException;
import com.lookstarry.common.to.mq.OrderTo;
import com.lookstarry.common.to.SkuHasStockVo;
import com.lookstarry.common.to.mq.StockLockDetailTo;
import com.lookstarry.common.to.mq.StockLockedTo;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.common.utils.Query;
import com.lookstarry.common.utils.R;
import com.lookstarry.doermail.ware.dao.WareOrderTaskDetailDao;
import com.lookstarry.doermail.ware.dao.WareSkuDao;
import com.lookstarry.doermail.ware.entity.WareOrderTaskDetailEntity;
import com.lookstarry.doermail.ware.entity.WareOrderTaskEntity;
import com.lookstarry.doermail.ware.entity.WareSkuEntity;
import com.lookstarry.doermail.ware.entity.WareSkuLeftEntity;
import com.lookstarry.doermail.ware.enume.OrderStatusEnum;
import com.lookstarry.doermail.ware.enume.WareLockStatus;
import com.lookstarry.doermail.ware.feign.OrderFeignService;
import com.lookstarry.doermail.ware.service.WareOrderTaskDetailService;
import com.lookstarry.doermail.ware.service.WareOrderTaskService;
import com.lookstarry.doermail.ware.service.WareSkuService;
import com.lookstarry.doermail.ware.vo.OrderItemVo;
import com.lookstarry.doermail.ware.vo.OrderVo;
import com.lookstarry.doermail.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    WareOrderTaskDetailDao orderTaskDetailDao;

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    OrderFeignService orderFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<WareSkuEntity>();
        String wareId = (String) params.get("wareId");
        String skuId = (String) params.get("skuId");
        System.out.println("skuId:" + skuId);
        if (StringUtils.isNotEmpty(wareId) && StringUtils.isNumeric(wareId) && !wareId.equalsIgnoreCase("0")) {
            queryWrapper.eq("ware_id", wareId);
        }
        if (StringUtils.isNotEmpty(skuId) && StringUtils.isNumeric(skuId) && !skuId.equalsIgnoreCase("0")) {
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
        if (skuIds != null && skuIds.size() != 0) {
            List<WareSkuLeftEntity> wareSkuLeftEntities = this.baseMapper.selectLeftStock(skuIds);
            Map<Long, Long> idToStock = wareSkuLeftEntities.stream().collect(Collectors.toMap(WareSkuLeftEntity::getSkuId, WareSkuLeftEntity::getStockLeft));
            List<SkuHasStockVo> skuHasStockVos = skuIds.stream().map((id) -> {
                SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
                skuHasStockVo.setSkuId(id);
                if (idToStock != null) {
                    skuHasStockVo.setHasStock(idToStock.get(id) == null ? false : idToStock.get(id) > 0);
                } else {
                    skuHasStockVo.setHasStock(false);
                }
                return skuHasStockVo;
            }).collect(Collectors.toList());
            return skuHasStockVos;
        }
        return null;
    }

    /**
     * 为某个订单锁定库存
     * (rollbackFor = NoStockException.class) 默认只要是运行时异常都会解锁库存
     * <p>
     * 库存有锁定，就要有解锁场景：核心就是库存锁定成功，之后都可能要进行解锁库存，一旦锁定成功，就发送一个消息，消息内容为库存锁定单
     * 库存解锁场景：
     * 1）下订单成功，但订单过期没有支付被系统自动取消或用户手动取消，都要解锁库存
     * 2）下订单成功，库存锁定成功，但是接下来例如扣减用户积分业务调用失败，导致订单回滚，之前锁定成功的库存也要进行解锁
     *
     * @param vo
     * @return
     */
    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        /**
         * 库存工作单详情，目的方便解锁库存
         */
        // 数据库保存库存工作单项
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskService.save(taskEntity);

        // 1、按照下单的收货地址，找到一个就近仓库，锁定库存

        // 1.1 找到每个商品在哪个仓库有库存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            Long skuId = item.getSkuId();
            Integer needNum = item.getCount();
            SkuWareHasStock stock = new SkuWareHasStock();
            stock.setLockNum(needNum);
            stock.setSkuId(skuId);
            // 查询这个商品在哪里有库存
            List<Long> wareIds = this.baseMapper.listWareIdHasSkuStock(skuId, needNum);
            stock.setWareIds(wareIds);
            return stock;
        }).collect(Collectors.toList());

        // 2、锁定库存
        for (SkuWareHasStock hasStock : collect) {
            Long skuId = hasStock.getSkuId();
            Integer lockNum = hasStock.getLockNum();
            List<Long> wareIds = hasStock.getWareIds();
            if (CollectionUtils.isEmpty(wareIds)) {
                // 如果有一件商品在任何仓库都没有剩余库存，则整个订单无效，抛出异常整个回滚
                throw new NoStockException(skuId);
            }
            Boolean lockSuccess = false;
            for (Long wareId : wareIds) {
                // 锁定成功，则返回受影响的行数为1，否则为0
                Long affectRow = this.baseMapper.lockSkuStock(wareId, skuId, lockNum);
                if (affectRow == 1L) {
                    // TODO 告诉MQ库存锁定成功，方便后续40分钟检查是否需要解锁库存
                    WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity(null,
                            skuId,
                            "",
                            hasStock.getLockNum(),
                            taskEntity.getId(),
                            wareId,
                            WareLockStatus.WARE_HAVE_LOCKED.getCode());
                    wareOrderTaskDetailService.save(taskDetailEntity);

                    // 库存锁定项，告知MQ该项锁定成功
                    // rabbitMQ库存锁定单详情
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    stockLockedTo.setWareTaskId(taskEntity.getId());
                    StockLockDetailTo stockLockDetailTo = new StockLockDetailTo();
                    BeanUtils.copyProperties(taskDetailEntity, stockLockDetailTo);
                    stockLockedTo.setStockLockDetailTo(stockLockDetailTo);

                    // 库存项锁定成功，就告知MQ该锁定项，方便后续订单取消解锁库存
                    rabbitTemplate.convertAndSend("stock-event-exchange",
                            "stock.locked",
                            stockLockedTo);
                    lockSuccess = true;
                    break;
                } else {
                    // 当前仓库锁定未成功，则尝试下一个仓库
                    continue;
                }
            }
            // 一旦有库存锁定失败，上述所有都会进行回滚
            if (!lockSuccess) {
                throw new NoStockException(skuId);
            }
        }
        return true;
    }

    @Override
    public void unlockStock(StockLockedTo to) {
        System.out.println("收到解锁库存的消息");
        Long wareTaskId = to.getWareTaskId(); // 库存工作单id
        Long lockDetailId = to.getStockLockDetailTo().getId();
        // 解锁
        // 1.查询数据库是否有关于这个订单的锁定库存项消息
        // 有：证明库存锁定成功，是否解锁
        //      1、查看订单，没有这个订单必须解锁，原因是锁定库存失败出现回滚，但消息已发出
        //      2、有这个订单，查看订单状态。
        //           订单状态： 已取消，解锁库存
        //                     没取消，不能解锁
        // 没有：库存锁定失败了，库存回滚了，这种情况无需解锁
        WareOrderTaskDetailEntity detailEntity = wareOrderTaskDetailService.getById(lockDetailId);
        if (detailEntity != null) {
            // 订单项存在
            WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(wareTaskId);
            String orderSn = taskEntity.getOrderSn();  // 根据订单号查询订单的状态
            R r = orderFeignService.getOrderStatus(orderSn);
            if (r.getCode() == 0) {
                OrderVo orderVo = r.getData(new TypeReference<OrderVo>() {});
                Integer status = null;
                if (orderVo != null) {
                    status = orderVo.getStatus();
                }
                if (orderVo == null || status.equals(OrderStatusEnum.CANCLED.getCode())) {
                    // 订单已经取消，才能解锁库存
                    // 订单项存在，订单不存在，可能是其他服务把订单回滚了
                    // 当前库存工作单详情，为已锁定但是未解锁才可以解锁
                    if(detailEntity.getLockStatus().equals(WareLockStatus.WARE_HAVE_LOCKED.getCode())){
                        unLockStock(detailEntity.getSkuId(), detailEntity.getWareId(), detailEntity.getSkuNum(),
                                 to.getStockLockDetailTo().getId());
                    }
                }
            } else {
                throw new RuntimeException("远程服务调用失败");
            }
        }else{
            // 订单项不存在，无需解锁
        }
    }

    /**
     * 防止订单服务卡顿，导致订单状态消息一直改不了，库存消息优先到期，查询订单新建状态，什么都做不了就消费了消息
     * 导致卡顿的订单，永远解锁不了库存
     * @param orderTo
     */
    @Transactional
    @Override
    public void unlockStock(OrderTo orderTo) {
        String orderSn = orderTo.getOrderSn();
        // 查一下最新库存的状态
        WareOrderTaskEntity orderTaskEntity = wareOrderTaskService.queryByOrderSn(orderSn);
        List<WareOrderTaskDetailEntity> lockedDetails = wareOrderTaskDetailService.getLockedBatchByTaskId(orderTaskEntity.getId());
        lockedDetails.stream().forEach((detailItem) -> {
            unLockStock(detailItem.getSkuId(), detailItem.getWareId(), detailItem.getSkuNum(), detailItem.getId());
        });
    }

    @Transactional
    public void unLockStock(Long skuId, Long wareId, Integer num, Long id) {
        wareSkuDao.unlockStock(skuId, wareId, num);
        WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity();
        taskDetailEntity.setId(id);
        taskDetailEntity.setLockStatus(WareLockStatus.WARE_HAVE_UNLOCKED.getCode());
        wareOrderTaskDetailService.updateById(taskDetailEntity);
    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private List<Long> wareIds;
        private Integer lockNum;
    }

}