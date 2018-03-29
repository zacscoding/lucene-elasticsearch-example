package org.esdemo.entity;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(indexName = "receipts", type = "receipt", shards = 1, replicas = 0, refreshInterval = "-1")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ReceiptEntity {

    @Id
    private String id;

    @Field(type = FieldType.keyword)
    private String from;

    @Field(type = FieldType.keyword)
    private String to;

    @Field(type = FieldType.Integer)
    private int price;
}
