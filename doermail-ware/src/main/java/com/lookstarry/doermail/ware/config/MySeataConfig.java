//package com.lookstarry.doermail.ware.config;
//
//import com.zaxxer.hikari.HikariDataSource;
//import io.seata.rm.datasource.DataSourceProxy;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.util.StringUtils;
//
//import javax.sql.DataSource;
//
///**
// * @PackageName:com.lookstarry.doermail.order.config
// * @NAME:MySeataConfig
// * @Description:
// * @author: yizhichangyuan
// * @date:2021/6/25 11:05
// */
//@Configuration
//public class MySeataConfig {
//    @Autowired
//    DataSourceProperties dataSourceProperties;
//
//    @Primary
//    @Bean
//    public DataSource dataSource(DataSourceProperties dataSourceProperties){
//        HikariDataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
//        if (StringUtils.hasText(dataSourceProperties.getName())) {
//            dataSource.setPoolName(dataSourceProperties.getName());
//        }
//
//        return new DataSourceProxy(dataSource);
//    }
//}
