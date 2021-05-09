package com.lookstarry.doermail.search.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @PackageName:com.lookstarry.doermail.search.entity
 * @NAME:Item
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/14 18:58
 */
@Data
@Document(indexName = "item")
public class Item {
    @Id
    String id;

    @Field(type= FieldType.Text)
    String title; //标题

    @Field(type=FieldType.Keyword)
    String category;// 分类

    @Field(type=FieldType.Keyword)
    String brand; // 品牌

    @Field(type=FieldType.Double)
    Double price; // 价格

    @Field(index=false, type=FieldType.Keyword)
    String images; // 图片地址
}
