package com.lookstarry.doermail.product.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @PackageName:com.lookstarry.doermail.product.config
 * @NAME:MybatisConfig
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/7 22:54
 */
@Configuration
@EnableTransactionManagement
@MapperScan("com.lookstarry.doermail.product.dao")
public class MybatisConfig {
    /**
     * mybatis的分页信息
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
