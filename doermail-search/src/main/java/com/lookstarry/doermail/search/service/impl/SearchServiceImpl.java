package com.lookstarry.doermail.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.lookstarry.common.constant.EsConstant;
import com.lookstarry.common.to.SkuEsModel;
import com.lookstarry.doermail.search.entity.SkuEsSaveModel;
import com.lookstarry.doermail.search.service.SearchService;
import com.lookstarry.doermail.search.vo.SearchParam;
import com.lookstarry.doermail.search.vo.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.InternalAggregations;
import org.elasticsearch.search.aggregations.bucket.nested.InternalNested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @PackageName:com.lookstarry.doermail.search.service.impl
 * @NAME:SearchServiceImpl
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/26 23:34
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public SearchResult search(SearchParam searchParam) {
        SearchQuery searchQuery = buildSearchQuery(searchParam);
        SearchResult searchResult = null;
        try{
            searchResult = elasticsearchTemplate.query(searchQuery, (ResultsExtractor<SearchResult>) response -> {
                return buildSearchResult(response, searchParam);
            });
        }catch(Exception e){
            e.printStackTrace();
        }
        return searchResult;
    }

    /**
     * 封装检索结果
     * @param response
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response, SearchParam searchParam) {
        SearchResult searchResult = new SearchResult();
        List products = new ArrayList<SkuEsModel>();
        // 1 封装商品列表
        SearchHits hits = response.getHits();
        if(hits != null && hits.getHits().length > 0){
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                // 判断是否按关键字高亮检索，如果是，就显示高亮，否则不显示
                if(StringUtils.isNotEmpty(searchParam.getKeyword())){
                    // 拿到高亮显示标题
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String skuTitleValue = skuTitle.getFragments()[0].string();
                    esModel.setSkuTitle(skuTitleValue);
                }
                products.add(esModel);
            }
        }
        searchResult.setProducts(products);

        // 2 封装分页信息
        long total = hits.getTotalHits();
        searchResult.setTotal(total);
        searchResult.setPageNum(searchParam.getPageNum());
        int totalPage = total % EsConstant.PRODUCT_PAGESIZE == 0 ? (int)total / EsConstant.PRODUCT_PAGESIZE : (int)total / EsConstant.PRODUCT_PAGESIZE + 1;
        searchResult.setTotalPage(totalPage);

        // 3 封装品牌信息
        List brandVos = new ArrayList<SearchResult.BrandVo>();
        LongTerms brand_agg = (LongTerms)response.getAggregations().get("brand_agg");
        List<LongTerms.Bucket> brand_aggBuckets = brand_agg.getBuckets();
        Map<Long, String> brandIdToValue = new HashMap<>();
        for (LongTerms.Bucket brand_aggBucket : brand_aggBuckets) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            long brandId = brand_aggBucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brandId);
            Map<String, Aggregation> aggregations = brand_aggBucket.getAggregations().getAsMap();
            StringTerms brand_img_agg = (StringTerms) aggregations.get("brand_img_agg");
            brandVo.setBrandImg(brand_img_agg.getBuckets().get(0).getKeyAsString());
            StringTerms brand_name_agg = (StringTerms) aggregations.get("brand_name_agg");
            String brandName = brand_name_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);
            brandVos.add(brandVo);
            brandIdToValue.put(brandId, brandName);
        }
        searchResult.setBrands(brandVos);

        // 4 封装分类信息
        List catelogVos = new ArrayList<>();
        LongTerms catelog_agg = (LongTerms)response.getAggregations().get("catelog_agg");
        List<LongTerms.Bucket> catelog_aggBuckets = catelog_agg.getBuckets();
        for (LongTerms.Bucket catelog_aggBucket : catelog_aggBuckets) {
            SearchResult.CatelogVo catelogVo = new SearchResult.CatelogVo();
            catelogVo.setCatelogId(catelog_aggBucket.getKeyAsNumber().longValue());
            Aggregations aggregations = catelog_aggBucket.getAggregations();
            StringTerms catelog_name_agg = (StringTerms) aggregations.get("catelog_name_agg");
            catelogVo.setCatelogName(catelog_name_agg.getBuckets().get(0).getKeyAsString()); // catelogId-catelogName 一对一
            catelogVos.add(catelogVo);
        }
        searchResult.setCatelogs(catelogVos);

        // 5 封装属性信息
        List attrVos = new ArrayList<SearchResult.AttrVo>();
        InternalNested attr_agg = (InternalNested) response.getAggregations().get("attr_agg");
        InternalAggregations aggregations = attr_agg.getAggregations();
        LongTerms attr_id_agg = (LongTerms) aggregations.get("attr_id_agg");
        Map<Long, String> attrIdToValue = new HashMap<>();
        List<LongTerms.Bucket> attr_id_agg_buckets = attr_id_agg.getBuckets();
        for (LongTerms.Bucket attr_id_agg_bucket : attr_id_agg_buckets) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            attrVo.setAttrId(attr_id_agg_bucket.getKeyAsNumber().longValue());
            Aggregations attrInternalAgg = attr_id_agg_bucket.getAggregations();
            StringTerms attr_name_agg = (StringTerms) attrInternalAgg.get("attr_name_agg");
            String attrName = attr_name_agg.getBuckets().get(0).getKeyAsString(); // attrId-attrName 一对一
            attrVo.setAttrName(attrName);
            StringTerms attr_value_agg = (StringTerms) attrInternalAgg.get("attr_value_agg");
            List<StringTerms.Bucket> attrValueBuckets = attr_value_agg.getBuckets(); // attrId-attrValue 一对多
            List<String> attrValues = attrValueBuckets.stream().map(item -> item.getKeyAsString()).collect(Collectors.toList());
            attrVo.setAttrValue(attrValues);
            attrVos.add(attrVo);
            attrIdToValue.put(attr_id_agg_bucket.getKeyAsNumber().longValue(), attrName);
        }
        searchResult.setAttrs(attrVos);

        // 6.构建面包屑导航功能
        if(searchParam.getAttrs() != null && searchParam.getAttrs().size() > 0){
            List<SearchResult.NavVo> navVos = searchParam.getAttrs().stream().map(attr -> {
                // 属性查询条件：attrs=2_5寸:6寸
                String[] s = attr.split("_");
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                navVo.setNavValue(s[1]);
                navVo.setNavName(attrIdToValue.get(Long.parseLong(s[0])));
                searchResult.getAttrIds().add(Long.parseLong(s[0]));
                // 取消掉该导航应该跳转的地址
                String link = replaceQueryString(searchParam, attr, "attrs");
                navVo.setLink(link);
                return navVo;
            }).collect(Collectors.toList());
            searchResult.setNavs(navVos);
        }

        // 品牌名导航
        if(searchParam.getBrandId() != null && searchParam.getBrandId().size() > 0){
            List<Long> brandIds = searchParam.getBrandId();
            List<SearchResult.NavVo> navs = searchResult.getNavs();
            for (Long brandId : brandIds) {
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                navVo.setNavName("品牌");
                navVo.setNavValue(brandIdToValue.get(brandId));
                String link = replaceQueryString(searchParam, brandId.toString(), "brandId");
                navVo.setLink(link);
                navs.add(navVo);
            }
            searchResult.setNavs(navs);
        }

        return searchResult;
    }

    private String replaceQueryString(SearchParam searchParam, String attr, String key) {
        try {
            String encode = URLEncoder.encode(attr, "UTF-8");
            encode = encode.replace("+", "%20");
            String replace = searchParam.get_queryString().replaceAll("&?" + key + "=" + encode, "");
            if(!replace.equals("")){
                if(replace.startsWith("&")){
                    replace = "?" + replace.substring(1);
                }else{
                    replace = "?" + replace;
                }
            }
            return "http://search.doermall.com/list.html" + replace;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检索请求参数
     * @param searchParam
     * @return
     */
    private SearchQuery buildSearchQuery(SearchParam searchParam) {
        /**
         * 查询：模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存）
         */
        // 1 构建bool-query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 1.1 must-模糊匹配 关键词查询
        if(StringUtils.isNotEmpty(searchParam.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", searchParam.getKeyword()));
        }
        // 1.2 must-filter 分类id查询
        if(searchParam.getCalalog3Id() != null){
            boolQuery.filter(QueryBuilders.termQuery("catelogId", searchParam.getCalalog3Id()));
        }
        // 1.3 must-filter 品牌id查询
        if(searchParam.getBrandId() != null && searchParam.getBrandId().size() > 0){
            boolQuery.filter(QueryBuilders.termsQuery("brandId", searchParam.getBrandId()));
        }
        // 1.3 must-filter 属性进行查询
        if(searchParam.getAttrs() != null && searchParam.getAttrs().size() > 0){
            // attrs=1_5寸:8寸&attrs=2_16G:8G
            // 每一个attr都必须生成一个nested查询
            for (String attr : searchParam.getAttrs()) {
                //attrs=1_5寸:8寸
                String[] s = attr.split("_");
                String attrId = s[0]; // 检索的属性id
                String[] attrValues = s[1].split(":"); // 检索属性值，可能有多个以冒号分隔
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }
        }
        // 1.4 must-filter 库存进行查询 0-无库存，1-有库存
        if(searchParam.getHasStock() != null){
            boolQuery.filter(QueryBuilders.termQuery("hasStock", searchParam.getHasStock() == 1 ? true : false));
        }
        // 1.5 must-filter 价格区间查询
        if(StringUtils.isNotEmpty(searchParam.getSkuPrice())){
            /**
             * skuPrice=1_500/_500/500_
             */
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String rangePrice = searchParam.getSkuPrice();
            String[] s = rangePrice.split("_");
            if(s.length == 2){ // 1_500  _500
                if(s[0].equals("_")){
                    rangeQuery.lte(s[1]);
                }else{
                    rangeQuery.gte(s[0]).lte(s[1]);
                }
            }else if(s.length == 1){ //500_
                rangeQuery.gte(s[0]);
            }
            boolQuery.filter(rangeQuery);
        }


        /**
         * 排序，分页，高亮
         */
        FieldSortBuilder fsb = null;
         // 2.1 排序 sort=saleCount_asc/desc
        if(StringUtils.isNotEmpty(searchParam.getSort())){
            String[] s = searchParam.getSort().split("_");
            fsb = SortBuilders.fieldSort(s[0]).order(s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC);
        }
        // 2.2 分页
        // pageNum: 1 from: 0 size: 5 [0,1,2,3,4]
        // pageNum: 2 from: 5 size: 5
        PageRequest page = PageRequest.of(searchParam.getPageNum() - 1, EsConstant.PRODUCT_PAGESIZE);

        // 2.3 高亮
        HighlightBuilder.Field highlightField = null;
        if(StringUtils.isNotEmpty(searchParam.getKeyword())){
            highlightField = new HighlightBuilder.Field("skuTitle").preTags("<b style='color:red'>").postTags("</b>");
        }

        /**
         * 聚合分析
         */
        // 3.1 品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg").field("brandId").size(50);
        // 品牌聚合的子聚合：聚合brandId对应的品牌名和图片地址
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        // 3.2 分类聚合
        TermsAggregationBuilder catelog_agg = AggregationBuilders.terms("catelog_agg").field("catelogId").size(20);
        catelog_agg.subAggregation(AggregationBuilders.terms("catelog_name_agg").field("catelogName").size(1));
        // 3.3 属性聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        // 聚合出所有的属性id
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(10);
        // 属性聚合的子聚合：聚合attr_id对应属性名和属性值
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        attr_agg.subAggregation(attr_id_agg);

        /**
         * 组装以上所有查询和聚合
         */
        NativeSearchQueryBuilder nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(boolQuery).withPageable(page);
        if(fsb != null){
            nativeSearchQuery.withSort(fsb);
        }
        if(highlightField != null){
            nativeSearchQuery.withHighlightFields(highlightField);
        }
        nativeSearchQuery.addAggregation(brand_agg);
        nativeSearchQuery.addAggregation(catelog_agg);
        nativeSearchQuery.addAggregation(attr_agg);

        SearchQuery searchQuery = nativeSearchQuery.build();
        return searchQuery;
    }
}
