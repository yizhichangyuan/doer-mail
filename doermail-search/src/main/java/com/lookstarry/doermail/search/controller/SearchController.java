package com.lookstarry.doermail.search.controller;

import com.lookstarry.doermail.search.service.SearchService;
import com.lookstarry.doermail.search.vo.SearchParam;
import com.lookstarry.doermail.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @PackageName:com.lookstarry.doermail.search.controller
 * @NAME:SearchController
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/26 23:13
 */
@Controller
public class SearchController {
    @Autowired
    private SearchService searchService;

    /**
     * 自动将get请求的所有参数自动封装为指定的对象
     * @param searchParam
     * @return
     */
    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model, HttpServletRequest request){
        // 根据传递来的页面查询参数，去ES中检索商品
        String queryString = request.getQueryString();
        searchParam.set_queryString(queryString);
        SearchResult result = searchService.search(searchParam);
        model.addAttribute("result", result);
        return "list";
    }
}
