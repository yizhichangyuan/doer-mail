package com.lookstarry.doermail.search;

import com.lookstarry.doermail.search.dao.EmployeeRepository;
import com.lookstarry.doermail.search.entity.Employee;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @PackageName:com.lookstarry.doermail.search
 * @NAME:EmployeeTest
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/15 17:45
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EmployeeTest {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void testSearch(){
        // 1.基本查询
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("address", "mill");
        // 2.第一个聚合查询：查询年龄分布，第一个为自定义聚合名称
        TermsAggregationBuilder aggBuilder = AggregationBuilders.terms("aggAge").field("age").size(10);
        // 3.第二个聚合查询：查询平均薪资
        AvgAggregationBuilder avgBuilder = AggregationBuilders.avg("avgBalance").field("balance");
        // 利用NativeSearchQueryBuilder将几个条件拼在一起
        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(queryBuilder).addAggregation(aggBuilder).addAggregation(avgBuilder).build();
        // 查询方法1：劣势：适用于简单的查询
        Page<Employee> employeePage = employeeRepository.search(searchQuery);
        List<Employee> employeeList = employeePage.getContent();
        employeeList.forEach(System.out::println);
        // 查询方法2：适用于复杂的查询，例如：bool查询或聚合查询
        // 这里因为是聚合查询，所以返回结果集应该选择AggregatedPage类型
        AggregatedPage<Employee> aggPage = this.elasticsearchTemplate.queryForPage(searchQuery, Employee.class);
        // 解析：获取命中总数和总分页数，用于前端分页
        long size = aggPage.getTotalElements();
        long pageNum = aggPage.getTotalPages();
        System.out.println("总共命中" + size + "个");
        System.out.println("总共" + pageNum + "页");
        List<Employee> employees = aggPage.getContent();
        employees.forEach(System.out::println);
        // 解析：解析第一个平均年龄聚合查询结果，因为是求年龄分布，操作为Terms，对应age属性类型就为LongTerms
        LongTerms agg = (LongTerms)aggPage.getAggregation("aggAge");
        List<LongTerms.Bucket> buckets = agg.getBuckets();
        buckets.forEach(item -> {
            System.out.println(item.getKeyAsString() + "岁共" + item.getDocCount() + "个");
        });
        // 解析：解析第二个平均薪资聚合查询结果，因为是平均薪资，操作为avg，对应薪资属性类型为IntegerAvg，没有LongAvg
        InternalAvg avg = (InternalAvg) aggPage.getAggregation("avgBalance");
        // 聚合名称
        String avgName = avg.getName();
        System.out.println(avgName + "平均薪资：" + avg.getValue());
    }
}
