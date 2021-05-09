package com.lookstarry.doermail.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @PackageName:com.lookstarry.doermail.search.vo
 * @NAME:SearchParam
 * @Description: 封装页面所有可能传递过来的检索参数数据
 * @author: yizhichangyuan
 * @date:2021/4/26 23:33
 */
@Data
public class SearchParam {
    private String keyword;

    private Long calalog3Id;

    /**
     * sort=saleCount_asc/desc
     * sort=skuPrice_asc/desc
     * sort=hostScore_asc/desc
     */
    private String sort = "hotScore_desc";

    /**
     * 过滤条件
     * hasStock（是否有货）、skuPrice（价格区间）、brandId、catalog3Id、attrs
     * hasStock=0/1
     * skuPrice=1_500/_500/500_
     * brandId=1&brandId=5 允许多选
     * attrs=2_5寸:6寸 下划线前为属性id，后面为属性value，多选属性以冒号分隔
     */
    private Integer hasStock; // 是否只显示有货，默认查询有库存
    private String skuPrice; // 价格区间
    private List<Long> brandId; // 品牌查询，可以多选
    /**
     * 属性查询条件：attrs=2_5寸:6寸
     */
    private List<String> attrs; // 按照属性进行筛选
    private Integer pageNum = 1;  // 页码

    private String _queryString; // 原生的所有查询条件

}
