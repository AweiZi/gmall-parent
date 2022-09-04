package com.atguigu.gmall.search.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
@Document(indexName = "person",shards = 1,replicas = 1)
@Data
public class Person {
    @Id
    private Long id;
    @Field(value = "first",type = FieldType.Keyword)//type的类型Text[村的时候不分词] keyword：都是字符串
    private String firstName;
    @Field(value = "last",type = FieldType.Keyword)
    private String lastName;
    @Field(value = "age")
    private Integer age;
    @Field(value = "address",analyzer ="ik_smart" )
    private String address;
}
