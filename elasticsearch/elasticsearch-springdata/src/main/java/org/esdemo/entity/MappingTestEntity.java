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
@Builder
@Document(indexName = "mapping-test", shards = 1, replicas = 0, refreshInterval = "-1")
// @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MappingTestEntity {

    @Id
    private String id;

    @Field(type = FieldType.keyword)
    private String fieldTypeKeyword;

    @Field(type = FieldType.text)
    private String fieldTypeText;

    @Field(type= FieldType.keyword, index = false)
    private String fieldTypeKeywordAndNoIndex;
}
