package com.lookstarry.doermail.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @PackageName:com.doermall.cart.vo
 * @NAME:Cart
 * @Description: 需要计算的属性需要重写get方法，保证每次获取都会重新进行计算
 * @author: yizhichangyuan
 * @date:2021/5/16 12:51
 */
public class Cart {
    private List<CartItem> cartItems;
    private Integer countNum;  // 商品总数
    private Integer countType; // 购物项类型数量
    private BigDecimal totalAmount = new BigDecimal(0);
    private BigDecimal reducePrice = new BigDecimal(0); // 优惠金额

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public Integer getCountNum() {
        int count = 0;
        for (CartItem cartItem : cartItems) {
            count += cartItem.getCount();
        }
        return count;
    }

    public Integer getCountType() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public BigDecimal getTotalAmount() {
        // 1、计算购物项总价
        BigDecimal total = new BigDecimal(0);
        for (CartItem cartItem : cartItems) {
            // 计算所有选中的商品价格
            if(cartItem.getChecked()){
                total = total.add(cartItem.getTotalPrice());
            }
        }
        // 2、减去优惠金额
        total = total.subtract(reducePrice);
        return total;
    }

    public BigDecimal getReducePrice() {
        return reducePrice;
    }

    public void setReducePrice(BigDecimal reducePrice) {
        this.reducePrice = reducePrice;
    }
}
