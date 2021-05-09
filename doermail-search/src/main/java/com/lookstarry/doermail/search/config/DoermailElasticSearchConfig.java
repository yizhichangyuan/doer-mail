//package com.lookstarry.doermail.search.config;
//
////import org.apache.http.HttpHost;
////import org.elasticsearch.client.*;
////import org.springframework.context.annotation.Bean;
//import org.elasticsearch.client.RequestOptions;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @PackageName:com.lookstarry.doermail.search.config
// * @NAME:DoermailElasticSearchConfig
// * @Description:
// * @author: yizhichangyuan
// * @date:2021/4/14 22:36
// * 1、导入依赖
// * 		<dependency>
// * 			<groupId>org.elasticsearch.client</groupId>
// * 			<artifactId>elasticsearch-rest-high-level-client</artifactId>
// * 			<version>7.4.2</version>
// * 		</dependency>
// * 2、编写配置，给容器注入一个RestHighLevelClient
// * 3、参照官方API https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high.html
// */
//@Configuration
//public class DoermailElasticSearchConfig {
//
//    public static final RequestOptions COMMON_OPTIONS;
//    static {
//        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
////        builder.addHeader("Authorization", "Bearer " + TOKEN);
////        builder.setHttpAsyncResponseConsumerFactory(
////                new HttpAsyncResponseConsumerFactory
////                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
//        COMMON_OPTIONS = builder.build();
//    }
//
////    @Bean
////    public RestHighLevelClient esRestClient(){
////        RestClientBuilder builder = null;
////        builder = RestClient.builder(new HttpHost("192.168.2.200", 9200, "http"));
////        RestHighLevelClient client = new RestHighLevelClient(builder);
//////        RestHighLevelClient client = new RestHighLevelClient(
//////                RestClient.builder(
//////                        new HttpHost("192.168.2.200", 9200, "http")));
////        return client;
////    }
//}
