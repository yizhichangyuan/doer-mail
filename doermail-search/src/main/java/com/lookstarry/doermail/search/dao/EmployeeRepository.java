package com.lookstarry.doermail.search.dao;

import com.lookstarry.doermail.search.entity.Employee;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @PackageName:com.lookstarry.doermail.search.dao
 * @NAME:EmployeeRepository
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/15 17:44
 */
public interface EmployeeRepository extends ElasticsearchRepository<Employee, Long> {
}
