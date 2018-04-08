package org.esdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;

/**
 * @author zacconding
 * @Date 2018-04-09
 * @GitHub : https://github.com/zacscoding
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Mapping(mappingPath = "/mappings/temp.json")
@Document(indexName = "temp-test", type = "test", shards = 1, replicas = 0, refreshInterval = "-1")
public class temp {

    @Id
    private String id;

    @Field(type = FieldType.keyword)
    private String keyword;
}
