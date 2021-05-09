package com.lookstarry.doermail.search;

import com.lookstarry.doermail.search.dao.EmployeeRepository;
import com.lookstarry.doermail.search.dao.ItemRepository;
import com.lookstarry.doermail.search.entity.Item;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @PackageName:com.lookstarry.doermail.search
 * @NAME:IndexTest
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/14 19:01
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class IndexTest {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void testCreate(){
         // 创建索引，会根据Item类中的
        elasticsearchTemplate.createIndex(Item.class);
        elasticsearchTemplate.putMapping(Item.class);
    }

    @Test
    public void testInsert(){
        Item item = new Item();
        item.setBrand("华为");
        item.setCategory("智能手机");
        item.setId("1");
        item.setImages("http://xxx.com");
        item.setPrice(100.0);
        item.setTitle("华为Mate40 徕卡双摄");
        itemRepository.index(item);
    }

    @Test
    public void testSearch(){
        MatchQueryBuilder builder = QueryBuilders.matchQuery("brand", "华为");
        Iterable<Item> items = itemRepository.search(builder);
        items.forEach(System.out::println);
    }


}
