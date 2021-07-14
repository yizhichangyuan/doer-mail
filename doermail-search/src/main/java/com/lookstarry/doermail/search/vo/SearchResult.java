package com.lookstarry.doermail.search.vo;

import com.lookstarry.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @PackageName:com.lookstarry.doermail.search.vo
 * @NAME:SearchResponse
 * @Description: 返回给页面的所有信息
 * @author: yizhichangyuan
 * @date:2021/4/27 15:10
 */
@Data
public class SearchResult {
    // 查询到的所有商品信息
    private List<SkuEsModel> products;

    /**
     * 分页信息
     */
    private Integer pageNum; // 当前页码
    private Long total; // 总记录数
    private Integer totalPage; // 总页码

    // ============返回给页面检索栏的检索信息=============
    private List<BrandVo> brands; // 当前查询到的结果所涉及的所有品牌
    private List<AttrVo> attrs; // 当前查询的结果所涉及到的所有属性
    private List<Long> attrIds = new ArrayList<>();
    private List<CatelogVo> catelogs; // 当前查询的结果所涉及的所有分类

    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    public static class CatelogVo{
        private Long catelogId;
        private String catelogName;
    }


    // 面包屑导航数据
    private List<NavVo> navs = new ArrayList<>();

    @Data
    public static class NavVo{
        private String navName;
        private String navValue;
        private String link;
    }


}
