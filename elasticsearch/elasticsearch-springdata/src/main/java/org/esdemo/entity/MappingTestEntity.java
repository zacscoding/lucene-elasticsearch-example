package org.esdemo.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Mapping(mappingPath = "/mappings/mapping-test-entity.json")
@Document(indexName = "mapping-test", type = "test", shards = 1, replicas = 0, refreshInterval = "-1")
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

    @Field(type = FieldType.text, index = false)
    private String fieldTypeTextAndNoIndex;

    /**
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-analyzers.html
     */
    //@Field(type = FieldType.keyword, analyzer = "")
    private String defaultAnalyzer;

    //@Field(type = FieldType.keyword, analyzer = "simple")
    private String simpleAnalyzer;

    //@Field(type = FieldType.keyword, analyzer = "whitespace")
    private String whitespaceAnalyzer;

    //@Field(type = FieldType.keyword, analyzer = "stop")
    private String stopAnalyzer;

    //@Field(type = FieldType.keyword, analyzer = "keyword")
    private String keywordAnalyzer;

    //@Field(type = FieldType.keyword, analyzer = "pattern")
    private String patternAnalyzer;
}
