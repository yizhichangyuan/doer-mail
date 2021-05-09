package com.lookstarry.doermail.search.dao;

import com.lookstarry.doermail.search.entity.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @PackageName:com.lookstarry.doermail.search.dao
 * @NAME:ItemRepository
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/14 23:41
 */
// 这里的String对应Item的主键类型
public interface ItemRepository extends ElasticsearchRepository<Item, String>{
}
