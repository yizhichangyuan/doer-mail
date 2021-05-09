package com.lookstarry.doermail.product.web;

import com.lookstarry.doermail.product.service.SkuInfoService;
import com.lookstarry.doermail.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.ExecutionException;

/**
 * @PackageName:com.lookstarry.doermail.product.web
 * @NAME:ItemController
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/5 17:45
 */
@Controller
public class ItemController {
    @Autowired
    private SkuInfoService skuInfoService;


    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model){
        System.out.println("准备查询" + skuId);
        SkuItemVo skuItemVo = null;
        try {
            skuItemVo = skuInfoService.item(skuId);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        model.addAttribute("item", skuItemVo);
        return "item";
    }
}
