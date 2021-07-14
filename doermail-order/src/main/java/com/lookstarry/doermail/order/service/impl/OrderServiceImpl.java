package com.lookstarry.doermail.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.lookstarry.common.exception.NoStockException;
import com.lookstarry.common.to.MemberInfoEntity;
import com.lookstarry.common.to.mq.OrderTo;
import com.lookstarry.common.to.mq.SeckillOrderTo;
import com.lookstarry.common.utils.R;
import com.lookstarry.doermail.order.constant.OrderConstant;
import com.lookstarry.doermail.order.entity.OrderItemEntity;
import com.lookstarry.doermail.order.entity.PaymentInfoEntity;
import com.lookstarry.doermail.order.enume.OrderCheckEnum;
import com.lookstarry.doermail.order.enume.OrderStatusEnum;
import com.lookstarry.doermail.order.feign.CartFeignService;
import com.lookstarry.doermail.order.feign.MemberFeignService;
import com.lookstarry.doermail.order.feign.ProductFeignService;
import com.lookstarry.doermail.order.feign.WareFeignService;
import com.lookstarry.doermail.order.interceptor.LoginUserInterceptor;
import com.lookstarry.doermail.order.service.OrderItemService;
import com.lookstarry.doermail.order.service.PaymentInfoService;
import com.lookstarry.doermail.order.to.FareTo;
import com.lookstarry.doermail.order.to.OrderCreateTo;
import com.lookstarry.doermail.order.to.SpuInfoTo;
import com.lookstarry.doermail.order.vo.*;
//import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.common.utils.Query;

import com.lookstarry.doermail.order.dao.OrderDao;
import com.lookstarry.doermail.order.entity.OrderEntity;
import com.lookstarry.doermail.order.service.OrderService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    OrderDao orderDao;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    PaymentInfoService paymentInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 订单确认页面信息：用户的收货地址信息、结算商品信息、
     * @return
     */
    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        MemberInfoEntity loginUser = LoginUserInterceptor.threadLocal.get();
//        System.out.println("主线程..." + Thread.currentThread().getId());
        // 主线程获取上下文环境
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        // 1、查询所有的收货地址列表
        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
//            System.out.println("member线程..." + Thread.currentThread().getId());
            // 子线程主动塞入父线程上下文环境
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> addresses = memberFeignService.getAddressById(loginUser.getId());
            orderConfirmVo.setAddress(addresses);
        }, executor);


        // 2、远程查询用户购物车中所有选中的购物项
        CompletableFuture<Void> getOrderItemsFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("购物车线程..." + Thread.currentThread().getId());
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> items = cartFeignService.currentUserCartItems();
            // feign在远程调用之前要构造请求，调用很多的拦截器
            // RequestInterceptor
            return items;
        }, executor).thenAcceptAsync((items) -> {
            List<Long> skuIds = items.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            R r = wareFeignService.getSkuHasStock(skuIds);
            if (r.getCode() == 0) {
                List<SkuStockVo> data = r.getData(new TypeReference<List<SkuStockVo>>() {
                });
                Map<Long, Boolean> hasStockMap = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                List<OrderItemVo> orderItems = items.stream().map((item) -> {
                    item.setHasStock(hasStockMap.get(item.getSkuId()));
                    return item;
                }).collect(Collectors.toList());
                orderConfirmVo.setOrderItems(orderItems);
            }
        }, executor);

        // 3、查询用户积分信息
        Integer integration = loginUser.getIntegration();
        orderConfirmVo.setIntegration(integration);

        // 4、订单总额、应付总额自动由get方法计算

        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + loginUser.getId(), token, 30, TimeUnit.MINUTES);
        // 给页面一个，用来提交订单校验
        orderConfirmVo.setOrderToken(token);

        CompletableFuture<Void> allFuture = CompletableFuture.allOf(getAddressFuture, getOrderItemsFuture);
        allFuture.get();

        // TODO 幂等性 5、订单防重复下单令牌
        return orderConfirmVo;
    }

    // 本地事务，在分布式系统，只能控制自己事务的回滚，无法控制其他服务的回滚
    // 分布式事务：最大原因：网络问题 + 分布式机器
//    @GlobalTransactional
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        SubmitOrderResponseVo response = new SubmitOrderResponseVo();
        response.setCode(OrderCheckEnum.ORDER_CREATE_SUCCESS.getCode());

        // 创建订单，验证防重令牌，验证价格，锁定库存...
        // 1.验证令牌，防止网络延迟用户重复点击造成重复下单【令牌的对比和删除必须保证原子性】
        // 0令牌事变 - 1删除成功
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = vo.getOrderToken();
        MemberInfoEntity loginUser = LoginUserInterceptor.threadLocal.get();
        String redisKey = OrderConstant.USER_ORDER_TOKEN_PREFIX + loginUser.getId();
        // 原子验证令牌和删除令牌
        Long result = (Long)redisTemplate.execute(new DefaultRedisScript(script, Long.class), Arrays.asList(redisKey), orderToken);
        if(result == 0L){
            // 令牌验证失败
            response.setCode(OrderCheckEnum.ORDER_TOKEN_FAIL.getCode());
        }else{
            // 令牌验证成功
            // 1、创建订单、订单项信息
            OrderCreateTo order = createOrder(vo);
            // 2、验价
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            if(Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01){
                // 金额对比
                // TODO 3、保存订单到数据库
                saveOrder(order);
                // TODO 4、库存锁定，只要有异常就回滚订单数据
                // 订单号，所有订单项信息(skuId，num，sku_name)
                // 为了保证高并发，库存服务自己回滚，可以发消息给库存服务；
                // 库存服务本身也可以使用自动解锁模式，使用消息队列来完成这种模式

                WareSkuLockVo lockVo = getWareSkuLockVo(order);
                R r = wareFeignService.orderLockStock(lockVo);
                // 库存锁定成功，但是Feign调用超时返回，导致订单回滚，库存未回滚
                if(r.getCode() == 0){
                    // 库存锁定成功
                    response.setOrderEntity(order.getOrder());
                    // TODO 5、远程扣减积分出现异常，库存也要进行解锁
//                    int i = 10 / 0;
                    // TODO 订单创建成功发送消息
                    rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getOrder());
                }else{
                    // 库存锁定失败
                    response.setCode(OrderCheckEnum.STOCK_LOCK_FAIL.getCode());
                    throw new NoStockException();
                }
            }else{
                // 验价失败
                response.setCode(OrderCheckEnum.CHECK_SALE_FAIL.getCode());
            }
        }
        return response;
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity orderEntity = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return orderEntity;
    }

    @Override
    public void closeOrder(OrderEntity entity) {
        // 查询当前这个订单的最新状态
        OrderEntity orderEntity = this.getById(entity.getId());
        if(orderEntity.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode())){
            // 关单
            OrderEntity updateEntity = new OrderEntity();
            updateEntity.setId(entity.getId());
            updateEntity.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(updateEntity);

            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderEntity, orderTo);
            // 发送一个消息给MQ，告知库存服务关单了
            try{
                // TODO 报送消息百分百发送出去，每一个消息都可以做好日志记录（给数据库保存每一个消息的详细信息）
                // TODO 定期扫描数据库将失败的消息再发送一遍
                rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderTo);
            }catch(Exception e){
                // TODO 将没发送成功的消息进行重试发送


            }
        }
    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        OrderEntity orderEntity = this.getOrderByOrderSn(orderSn);
        PayVo payVo = new PayVo();

        String payAmount = orderEntity.getPayAmount().setScale(2, BigDecimal.ROUND_UP).toString();
        payVo.setTotal_amount(payAmount);
        payVo.setOut_trade_no(orderEntity.getOrderSn());

        List<OrderItemEntity> items = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        OrderItemEntity orderItemEntity = items.get(0);
        payVo.setSubject(orderItemEntity.getSkuName());
        payVo.setBody(orderItemEntity.getSkuAttrsVals());

        return payVo;
    }

    @Override
    public PageUtils queryPageWithOrderItem(Map<String, Object> params) {
        MemberInfoEntity memberVo = LoginUserInterceptor.threadLocal.get();
        IPage<OrderEntity> page = this.page(new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id", memberVo.getId()).orderByDesc("create_time"));

        List<OrderEntity> orderList = page.getRecords();
        List<String> orderSnList = orderList.stream().map(item -> item.getOrderSn()).collect(Collectors.toList());
        List<OrderItemEntity> orderItems = orderItemService.list(new QueryWrapper<OrderItemEntity>().in("order_sn", orderSnList));

        orderList = orderList.stream().map(order -> {
            List<OrderItemEntity> collectItems = orderItems.stream().filter(item -> item.getOrderSn().equals(order.getOrderSn())).collect(Collectors.toList());
            order.setOrderItems(collectItems);
            return order;
        }).collect(Collectors.toList());
        page.setRecords(orderList);

        return new PageUtils(page);
    }

    /**
     * 处理支付宝的支付结果
     * @param asyncVo
     * @return
     */
    @Transactional
    @Override
    public String handlePayResult(PayAsyncVo asyncVo) {
        // 1、保存交易流水
        PaymentInfoEntity paymentEntity = new PaymentInfoEntity();
        paymentEntity.setAlipayTradeNo(asyncVo.getTrade_no()); // 支付宝流水号
        paymentEntity.setOrderSn(asyncVo.getOut_trade_no());
        paymentEntity.setPaymentStatus(asyncVo.getTrade_status());
        paymentEntity.setCallbackTime(asyncVo.getNotify_time());
        paymentEntity.setTotalAmount(new BigDecimal(asyncVo.getTotal_amount()));
        paymentEntity.setSubject(asyncVo.getSubject());

        paymentInfoService.save(paymentEntity);

        // 2、修改订单状态信息
        Integer result = null;
        String tradeStatus = asyncVo.getTrade_status();
        if(tradeStatus.equals("TRADE_SUCCESS") || tradeStatus.equals("TRADE_FINISHED")){
            String orderSn = asyncVo.getOut_trade_no();
            this.baseMapper.updateOrderStatus(orderSn, OrderStatusEnum.PAYED.getCode());
            return "success";
        }


        return "fail";
    }


    private WareSkuLockVo getWareSkuLockVo(OrderCreateTo order) {
        WareSkuLockVo lockVo = new WareSkuLockVo();
        lockVo.setOrderSn(order.getOrder().getOrderSn());
        List<OrderItemVo> lockItems = order.getItems().stream().map((item) -> {
            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setSkuId(item.getSkuId());
            orderItemVo.setCount(item.getSkuQuantity());
            orderItemVo.setTitle(item.getSkuName());
            return orderItemVo;
        }).collect(Collectors.toList());
        lockVo.setLocks(lockItems);
        return lockVo;
    }

    /**
     * 保存订单数据到数据库
     * @param order
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        List<OrderItemEntity> items = order.getItems();

        this.save(orderEntity);
        System.out.println(items);
        orderItemService.saveBatch(items);
    }

    private OrderCreateTo createOrder(OrderSubmitVo vo){
        OrderCreateTo orderCreateTo = new OrderCreateTo();

        // 1、生成订单号
        String orderSn = IdWorker.getTimeId();// 可以保证生成的订单号全局唯一
        // 2、创建订单实体类
        OrderEntity orderEntity = buildOrder(vo.getAttrId(), orderSn);
        // 3、创建订单项信息
        List<OrderItemEntity> orderItems = buildOrderItems(orderSn);

        // 4、计算价格相关
        computePrice(orderEntity, orderItems);

        orderCreateTo.setOrder(orderEntity);
        orderCreateTo.setItems(orderItems);
        orderCreateTo.setPayPrice(orderEntity.getPayAmount());
        orderCreateTo.setFare(orderEntity.getFreightAmount());

        return orderCreateTo;
    }

    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItems) {
        // 1、订单价格相关数据
        BigDecimal total = new BigDecimal("0.0");
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");
        BigDecimal gift = new BigDecimal("0.0");
        BigDecimal growth = new BigDecimal("0.0");

        // 叠加每个订单项的计算优惠后的价格总额信息
        for (OrderItemEntity orderItem : orderItems) {
            coupon = coupon.add(orderItem.getCouponAmount());
            integration = integration.add(orderItem.getIntegrationAmount());
            promotion = promotion.add(orderItem.getPromotionAmount());
            total = total.add(orderItem.getRealAmount());
            gift = gift.add(new BigDecimal(orderItem.getGiftGrowth()));
            growth = growth.add(new BigDecimal(orderItem.getGiftGrowth()));
        }

        // 订单总额
        orderEntity.setTotalAmount(total);
        // 设置应付总额：订单总额 + 运费
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setCouponAmount(coupon);

        // 设置积分信息
        orderEntity.setGrowth(growth.intValue());
        orderEntity.setIntegration(gift.intValue());

        orderEntity.setDeleteStatus(0); // 订单删除状态：未删除
    }

    /**
     * 构建订单数据
     * @param addrId
     * @param orderSn
     * @return
     */
    private OrderEntity buildOrder(Long addrId, String orderSn) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderSn);

        // 会员信息
        MemberInfoEntity user = LoginUserInterceptor.threadLocal.get();
        orderEntity.setMemberId(user.getId());

        // 获取收货地址信息
        R r = wareFeignService.getFare(addrId);
        FareTo fareTo = r.getData(new TypeReference<FareTo>() {});
        // 设置运费信息
        orderEntity.setFreightAmount(fareTo.getFare());
        // 设置收货人信息
        orderEntity.setReceiverCity(fareTo.getAddress().getCity());
        orderEntity.setReceiverDetailAddress(fareTo.getAddress().getDetailAddress());
        orderEntity.setReceiverPhone(fareTo.getAddress().getPhone());
        orderEntity.setReceiverPostCode(fareTo.getAddress().getPostCode());
        orderEntity.setReceiverProvince(fareTo.getAddress().getProvince());
        orderEntity.setReceiverRegion(fareTo.getAddress().getRegion());

        // 设置订单的相关状态信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7); // 订单自动确认时间：7天
        orderEntity.setCreateTime(new Date());

        return orderEntity;
    }

    /**
     * 构建所有订单项实体类
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        // 最后一次确定每个购物项的价格
        List<OrderItemVo> currentUserCartItems = cartFeignService.currentUserCartItems();
        if(currentUserCartItems != null && currentUserCartItems.size() > 0){
            List<OrderItemEntity> collect = currentUserCartItems.stream().map((cartItem) -> {
                OrderItemEntity orderItemEntity = buildOrderItem(orderSn, cartItem);
                return orderItemEntity;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    /**
     * 构建单个订单项实体类
     *
     * @param orderSn
     * @param cartItem
     * @return
     */
    private OrderItemEntity buildOrderItem(String orderSn, OrderItemVo cartItem) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        // 1、订单信息：订单号
        orderItemEntity.setOrderSn(orderSn);
        // 2、商品Spu信息
        R r = productFeignService.getSpuInfoBySkuId(cartItem.getSkuId());
        SpuInfoTo spuInfo = r.getData(new TypeReference<SpuInfoTo>() {});
        orderItemEntity.setSpuId(spuInfo.getId());
        orderItemEntity.setSpuBrand(spuInfo.getBrandId().toString());
        orderItemEntity.setSpuName(spuInfo.getSpuName());
        orderItemEntity.setCategoryId(spuInfo.getCatelogId());

        // 3、商品Sku信息
        orderItemEntity.setSkuId(cartItem.getSkuId());
        orderItemEntity.setSkuName(cartItem.getTitle());
        orderItemEntity.setSkuPic(cartItem.getDefaultImage());
        orderItemEntity.setSkuPrice(cartItem.getUnitPrice());
        String skuAttrs = StringUtils.collectionToDelimitedString(cartItem.getSaleAttr(), ";");
        orderItemEntity.setSkuAttrsVals(skuAttrs);
        orderItemEntity.setSkuQuantity(cartItem.getCount());

        // 4、优惠信息（不做）
        // 5、积分信息
        orderItemEntity.setGiftGrowth(cartItem.getUnitPrice().multiply(new BigDecimal(cartItem.getCount())).intValue());
        orderItemEntity.setGiftIntegration(cartItem.getUnitPrice().multiply(new BigDecimal(cartItem.getCount())).intValue());

        // 6、该订单项的价格信息
        orderItemEntity.setPromotionAmount(new BigDecimal("0"));
        orderItemEntity.setCouponAmount(new BigDecimal("0"));
        orderItemEntity.setIntegrationAmount(new BigDecimal("0"));
        // 当前订单项的实际金额
        BigDecimal origin = cartItem.getUnitPrice().multiply(new BigDecimal(cartItem.getCount()));
        BigDecimal realPrice = origin.subtract(orderItemEntity.getCouponAmount())
                .subtract(orderItemEntity.getPromotionAmount())
                .subtract(orderItemEntity.getIntegrationAmount());
        orderItemEntity.setRealAmount(realPrice);

        return orderItemEntity;
    }

    @Transactional
    @Override
    public void createSeckillOrder(SeckillOrderTo secKillOrder) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(secKillOrder.getOrderSn());
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setCreateTime(new Date());
        orderEntity.setMemberId(secKillOrder.getMemberId());
        orderEntity.setPayAmount(secKillOrder.getSeckillPrice().multiply(
                new BigDecimal(secKillOrder.getNum())));
        this.save(orderEntity);

        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrderSn(secKillOrder.getOrderSn());
        orderItemEntity.setRealAmount(orderEntity.getPayAmount());
        orderItemEntity.setSkuQuantity(secKillOrder.getNum());

        R r = productFeignService.getSpuInfoBySkuId(secKillOrder.getSkuId());
        if(r.getCode() == 0){
            SpuInfoTo spuInfo = r.getData(new TypeReference<SpuInfoTo>() {});
            orderItemEntity.setSpuId(spuInfo.getId());
            orderItemEntity.setSpuBrand(spuInfo.getBrandId().toString());
            orderItemEntity.setSpuName(spuInfo.getSpuName());
            orderItemEntity.setCategoryId(spuInfo.getCatelogId());
        }
        r = productFeignService.getSkuInfoBySkuId(secKillOrder.getSkuId());
        if(r.getCode() == 0){
            SkuInfoVo skuInfoVo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
            });
            orderItemEntity.setSkuId(skuInfoVo.getSkuId());
            orderItemEntity.setSkuName(skuInfoVo.getSkuName());
            orderItemEntity.setSkuPic(skuInfoVo.getSkuDefaultImg());
            orderItemEntity.setSkuPrice(secKillOrder.getSeckillPrice());
        }
        orderItemService.save(orderItemEntity);
    }

    @Transactional // a事务的所有设置就传播到了和它公用一个事务的方法
    public void a(){
        OrderServiceImpl orderService = (OrderServiceImpl)AopContext.currentProxy();
        orderService.b();
        orderService.c();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void b(){

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void c(){

    }



}