package com.lookstarry.doermail.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lookstarry.doermail.cart.feign.ProductFeignService;
import com.lookstarry.doermail.cart.interceptor.CartInterceptor;
import com.lookstarry.doermail.cart.service.CartService;
import com.lookstarry.doermail.cart.vo.Cart;
import com.lookstarry.doermail.cart.vo.CartItem;
import com.lookstarry.doermail.cart.vo.SkuInfoVo;
import com.lookstarry.doermail.cart.vo.UserInfoVo;
import com.lookstarry.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.lookstarry.common.constant.CartConstant.CART_REDIS_KEY;

/**
 * @PackageName:com.doermall.cart.service.impl
 * @NAME:CartServiceImpl
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/16 17:10
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    private final String CART_PREFIX = "doermall:cart:";

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        CartItem cartItem = new CartItem();
        BoundHashOperations cartOps = getCartOps();
        String cartItemString = (String)cartOps.get(skuId.toString());
        if(StringUtils.isNotEmpty(cartItemString)){
            // 购物项已存在购物车中，则直接改变数量即可
            cartItem = JSON.parseObject(cartItemString, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
        }else{
            // 不存在，则异步编排查询sku信息及其属性
            CartItem finalCartItem = cartItem;
            CompletableFuture<Void> skuInfoTask = CompletableFuture.runAsync(() -> {
                R r = productFeignService.getSkuInfo(skuId);
                if (r.getCode() == 0) {
                    SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                    });
                    finalCartItem.setChecked(true);
                    finalCartItem.setCount(num);
                    finalCartItem.setSkuId(skuId);
                    finalCartItem.setDefaultImage(skuInfo.getSkuDefaultImg());
                    finalCartItem.setTitle(skuInfo.getSkuTitle());
                    finalCartItem.setUnitPrice(skuInfo.getPrice());
                }
            }, executor);

            CompletableFuture<Void> getSkuSaleAttrTask = CompletableFuture.runAsync(() -> {
                List<String> list = productFeignService.getSkuSaleAttrValues(skuId);
                finalCartItem.setSaleAttr(list);
            }, executor);

            CompletableFuture<Void> all = CompletableFuture.allOf(skuInfoTask, getSkuSaleAttrTask);
            all.get();
        }

        String s = JSON.toJSONString(cartItem);
        cartOps.put(cartItem.getSkuId().toString(), s);
        cartItem.setCount(num); // 返回给页面的数据为新添加的数据
        return cartItem;
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations cartOps = getCartOps();
        String cartItemString = (String)cartOps.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(cartItemString, CartItem.class);
        return cartItem;
    }

    /**
     * 获取用户购物车中的所有数据
     * 用户登陆：利用user-key获取临时购物车数据
     * 如果临时购物车有数据，则说明用户第一次登陆，将用户临时购物车的数据合并到登陆购物车数据中，并删除临时购物车
     * 如果临时购物车无数据，说明用户一直为登陆状态，则只需取出登陆购物车数据
     * 用户未登录：根据user-key获取临时购物车数据
     * @return
     */
    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();
        UserInfoVo userInfoVo = CartInterceptor.THREAD_LOCAL.get();
        if(userInfoVo.getUserId() != null){
            // 用户已登录，查看临时购物车中是否有数据
            String cartKey = CART_REDIS_KEY + userInfoVo.getUserKey();
            List<CartItem> cartItems = getCartItems(cartKey);
            if(cartItems != null){
                // 临时购物车有数据，则进行合并到登陆购物车中
                for (CartItem cartItem : cartItems) {
                    addToCart(cartItem.getSkuId(), cartItem.getCount());
                }
                // 合并完将临时购物车删除
                redisTemplate.delete(cartKey);
            }
            // 获取在线购物车数据
            cartKey = CART_REDIS_KEY + userInfoVo.getUserId();
            cartItems = getCartItems(cartKey);
            cart.setCartItems(cartItems);
        }else{
            // 用户未登录，查询临时购物车所有数据
            String cartKey = CART_REDIS_KEY + userInfoVo.getUserKey();
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setCartItems(cartItems);
        }
        return cart;
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setChecked(check == 1 ? true : false);
        String s = JSON.toJSONString(cartItem);
        BoundHashOperations cartOps = getCartOps();
        cartOps.put(skuId.toString(), s);
    }

    @Override
    public void modifySkuCount(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        BoundHashOperations cartOps = getCartOps();
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    private BoundHashOperations getCartOps() {
        UserInfoVo userInfoVo = CartInterceptor.THREAD_LOCAL.get();
        String cartKey = CART_REDIS_KEY;
        if(userInfoVo.getUserId() != null){
            // 用户已经登陆
            cartKey += userInfoVo.getUserId();
        }else{
            // 未登录
            cartKey += userInfoVo.getUserKey();
        }
        // 绑定该key值的hash操作
        BoundHashOperations<String, String, String> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }

    private List<CartItem> getCartItems(String cartKey){
        // 取出对应key的hash结构所有的values
        List<Object> values = redisTemplate.opsForHash().values(cartKey);
        if(values != null){
            List<CartItem> collect = values.stream().map((obj) -> {
                CartItem cartItem = JSON.parseObject((String) obj, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    /**
     * 获取用户购物车所有被选中的购物项
     * @return
     */
    @Override
    public List<CartItem> getUserCartItems() {
        UserInfoVo userInfoVo = CartInterceptor.THREAD_LOCAL.get();
        if(userInfoVo.getUserId() == null){
            return null;
        }
        String cartKey = CART_REDIS_KEY + userInfoVo.getUserId();
        List<CartItem> cartItems = getCartItems(cartKey);

        // 筛选出用户所有选中的购物项
        List<CartItem> checkItems = cartItems.stream().filter((cartItem) -> cartItem.getChecked()).collect(Collectors.toList());

        List<Long> skuIds = checkItems.stream().map((item) -> item.getSkuId()).collect(Collectors.toList());
        // 购物车价格更新，redis中购物项可能很早之前添加的，所以要重新更新价格
        R r = productFeignService.getSkuByIds(skuIds);
        Map<Long, BigDecimal> skuPriceMap = null;
        if(r.getCode() == 0){
            skuPriceMap = r.getData(new TypeReference<Map<Long, BigDecimal>>(){});
        }

        Map<Long, BigDecimal> finalSkuPriceMap = skuPriceMap;
        List<CartItem> collect = checkItems.stream().map((item) -> {
            // 设置最新从数据库查询的价格
            item.setUnitPrice(finalSkuPriceMap.get(item.getSkuId()));
            return item;
        }).collect(Collectors.toList());
        return collect;
    }
}
