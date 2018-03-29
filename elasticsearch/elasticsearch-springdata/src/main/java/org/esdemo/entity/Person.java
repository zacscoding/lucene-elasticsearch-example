package org.esdemo.entity;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(indexName = "persons", type = "person", shards = 1, replicas = 0, refreshInterval = "-1")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Person {
    @Id
    private String id;

    @Field(type = FieldType.keyword)
    private String name;

    @Field(type = FieldType.Integer)
    private Integer age;
}