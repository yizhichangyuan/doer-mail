package com.lookstarry.doermail.cart.service;

import com.lookstarry.doermail.cart.vo.Cart;
import com.lookstarry.doermail.cart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @PackageName:com.doermall.cart.service
 * @NAME:CartService
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/16 17:10
 */
public interface CartService {
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartItem getCartItem(Long skuId);

    Cart getCart() throws ExecutionException, InterruptedException;

    void checkItem(Long skuId, Integer check);

    void modifySkuCount(Long skuId, Integer num);

    void deleteItem(Long skuId);

    List<CartItem> getUserCartItems();

}
