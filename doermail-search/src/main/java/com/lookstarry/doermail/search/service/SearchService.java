package com.lookstarry.doermail.search.service;

import com.lookstarry.doermail.search.vo.SearchParam;
import com.lookstarry.doermail.search.vo.SearchResult;

public interface SearchService {
    /**
     *
     * @param searchParam 页面提交的检索条件
     * @return 检索的所有结果信息：商品信息、分页信息、检索栏信息
     */
    SearchResult search(SearchParam searchParam);
}
