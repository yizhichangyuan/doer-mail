package com.lookstarry.doermail.search.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @PackageName:com.lookstarry.doermail.search.entity
 * @NAME:Employee
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/15 17:39
 */
@Data
@Document(indexName = "newbank", type = "account")
public class Employee {
    @Id
    private Long accountNumber;
//
//    @Field(type=FieldType.Long)
//    private Long account;

    @Field(type= FieldType.Text)
    private String address;

    @Field(type=FieldType.Long)
    private Long age;

    @Field(type=FieldType.Long)
    private Long balance;

    @Field(type=FieldType.Text)
    private String city;

    @Field(type=FieldType.Text)
    private String email;

    @Field(type=FieldType.Text)
    private String employer;

    @Field(type=FieldType.Text)
    private String firstname;

    @Field(type=FieldType.Text)
    private String gender;

    @Field(type=FieldType.Text)
    private String lastname;

    @Field(type=FieldType.Text)
    private String state;
}
