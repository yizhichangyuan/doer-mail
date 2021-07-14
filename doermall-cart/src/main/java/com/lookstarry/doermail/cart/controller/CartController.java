package com.lookstarry.doermail.cart.controller;

import com.lookstarry.doermail.cart.service.CartService;
import com.lookstarry.doermail.cart.vo.Cart;
import com.lookstarry.doermail.cart.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @PackageName:com.doermall.cart.controller
 * @NAME:CartController
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/16 17:17
 */
@Controller
public class CartController {
    @Autowired
    CartService cartService;

    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<CartItem> currentUserCartItems(){
        List<CartItem> cartItems = cartService.getUserCartItems();
        return cartItems;
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.doermall.com/cart.html";
    }

    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId,
                                 @RequestParam("num") Integer num){
        cartService.modifySkuCount(skuId, num);
        return "redirect:http://cart.doermall.com/cart.html";

    }


    @GetMapping("/checkItem")
    public String check(@RequestParam("skuId") Long skuId,
                        @RequestParam("check") Integer check){
        cartService.checkItem(skuId, check);
        return "redirect:http://cart.doermall.com/cart.html";
    }
    /**
     * 浏览器端有一个cookie user-key来控制未登录的用户标识，用来标识用户购物车，一个月后过期
     * 如果第一次使用jd的购物车功能，都会给一个临时的用户身份；
     * 浏览器以后保存，每次访问都会带上这个cookie
     *
     * 登陆session有
     * 没登录，按照cookie带来的user-key来做
     * 没登录第一次访问购物车，会帮忙创建一个临时用户
     * @return
     */
    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);
        return "cartList";
    }

    /**
     * 重定向保证幂等性，不会重复添加购物项到购物车中
     * redirectAttributes.addFlashAttribute是将参数放入到session中，可以在页面中取出，但是只能取一次，刷新后就会消失
     * redirectAttributes.addAttribute是将参数作为get参数添加到重定向的url后
     * 添加商品到购物车
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num,
                            RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        CartItem cartItem = cartService.addToCart(skuId, num);
        redirectAttributes.addAttribute("skuId", cartItem.getSkuId());
        redirectAttributes.addAttribute("addCount", cartItem.getCount());
        return "redirect:http://cart.doermall.com/addToCartSuccess.html";
    }

    @GetMapping("addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, @RequestParam("addCount") Integer addCount, Model model){
        CartItem cartItem = cartService.getCartItem(skuId);
        cartItem.setCount(addCount); // 查询出的为该购物项总数量，改为新添加的数量展示在新增加页面
        model.addAttribute("item", cartItem);
        return "success";
    }
}
