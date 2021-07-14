package com.lookstarry.doermail.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @PackageName:com.lookstarry.doermail.order.web
 * @NAME:PageController
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/20 09:48
 */
@Controller
public class PageController {
    @GetMapping("/{page}.html")
    public String page(@PathVariable("page") String page){
        return page;
    }
}
