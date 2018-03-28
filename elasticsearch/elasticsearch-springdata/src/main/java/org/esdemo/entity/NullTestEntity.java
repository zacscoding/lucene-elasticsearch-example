package org.esdemo.entity;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.elasticsearch.common.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "null-test-entity", shards = 1, replicas = 0, refreshInterval = "-1")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NullTestEntity extends AbstractEntity {
    @Id
    private String id;

    @Field(type = FieldType.keyword)
    private String name;

    @Nullable
    @Field(type = FieldType.keyword)
    private String hobby;

    @Field(type = FieldType.Integer)
    private Integer age;
}
