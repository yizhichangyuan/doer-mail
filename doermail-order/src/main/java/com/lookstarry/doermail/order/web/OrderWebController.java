package com.lookstarry.doermail.order.web;

import com.lookstarry.common.exception.NoStockException;
import com.lookstarry.doermail.order.enume.OrderCheckEnum;
import com.lookstarry.doermail.order.service.OrderService;
import com.lookstarry.doermail.order.vo.OrderConfirmVo;
import com.lookstarry.doermail.order.vo.OrderSubmitVo;
import com.lookstarry.doermail.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

/**
 * @PackageName:com.lookstarry.doermail.order.web
 * @NAME:OrderWebController
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/20 11:30
 */
@Controller
public class OrderWebController {
    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData", orderConfirmVo);
        // 展示订单页信息
        return "confirm";
    }

    /**
     * 下单功能
     * @param vo
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes){
        try{
            // 下单成功来到支付选择页
            // 下单事变回到订单确认页重新确认订单信息
            SubmitOrderResponseVo submitOrderResp = orderService.submitOrder(vo);
            if(submitOrderResp.getCode() == 0){
                // 下单成功来到支付选择页
                model.addAttribute("submitOrderResp", submitOrderResp);
                return "pay.html";
            }else{
                String msg = "下单失败；";
                switch (submitOrderResp.getCode()){
                    case 1 : msg += "订单信息过期，请刷新再次提交"; break;
                    case 2 : msg += "订单商品价格发生变化，请确认后再次提交"; break;
                    case 3 : msg += "库存锁定失败，商品库存不足"; break;
                }
                // addFlashAttribute是模拟从session放数据
                redirectAttributes.addFlashAttribute("msg", msg);
                System.out.println("订单错误：" + msg);
                return "redirect:http://order.doermall.com/toTrade";
            }
        }catch(Exception e){
            if(e instanceof NoStockException){
                String msg = ((NoStockException) e).getMessage();
                System.out.println("订单错误：" + msg);
                redirectAttributes.addFlashAttribute("msg", msg);
            }
            System.out.println("订单错误：" + e.getMessage());
            return "redirect:http://order.doermall.com/toTrade";
        }
    }
}
