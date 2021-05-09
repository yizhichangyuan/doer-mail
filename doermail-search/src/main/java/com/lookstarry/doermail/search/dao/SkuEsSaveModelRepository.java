package com.lookstarry.doermail.search.dao;

import com.lookstarry.doermail.search.entity.SkuEsSaveModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @PackageName:com.lookstarry.doermail.search.dao
 * @NAME:SkuEsSaveModelRepository
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/16 23:40
 */
public interface SkuEsSaveModelRepository extends ElasticsearchRepository<SkuEsSaveModel, Long> {
}
