package org.esdemo.save;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.esdemo.AbstractTestRunner;
import org.esdemo.util.SimpleLogger;
import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author zacconding
 * @Date 2018-04-03
 * @GitHub : https://github.com/zacscoding
 */
public class BulkRequestTest extends AbstractTestRunner {

    @Test
    public void test() throws Exception {
        super.clearIndex(BulkRequestEntity.class);

        BulkProcessor bulkProcessor = BulkProcessor.builder(client, new Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                // SimpleLogger.println("beforeBulk : {} / {}", executionId, request.numberOfActions());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
//                SimpleLogger.println("afterBulk : {}, hasFailures : {}", executionId, response.hasFailures());

                for (BulkItemResponse item : response.getItems()) {
//                    SimpleLogger.build().appendRepeat(20, "==").append("Result : " + item.isFailed()).appendRepeat(20, "==").newLine()
//                                .appendln(SimpleLogger.toJsonWithPretty(item))
//                                .appendRepeat(20, "====").newLine().flush();
                    SimpleLogger.println("num of actions : {}, item id : {} , result : {}", request.numberOfActions(), item.getItemId(), item.isFailed());
                }
            }
            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                // SimpleLogger.println("afterBulk : {}, throwable message : {}", executionId, failure.getMessage());
            }
        }).setBulkActions(4).setConcurrentRequests(0).build();

        ObjectMapper mapper = new ObjectMapper();
        final String index = "bulk-request-test";
        final String type = "test";

        /*
        == afterBulk result if setBulkActions(4)
        num of actions : 4, item id : 0 , result : false
        num of actions : 4, item id : 1 , result : true
        num of actions : 4, item id : 2 , result : false
        num of actions : 4, item id : 3 , result : true

        == afterBulk result if setBulkActions(2)
        num of actions : 2, item id : 0 , result : false
        num of actions : 2, item id : 1 , result : true
        num of actions : 2, item id : 0 , result : false
        num of actions : 2, item id : 1 , result : true
         */
        // success : 0
        bulkProcessor.add(new IndexRequest(index, type).source(mapper.writeValueAsBytes(BulkRequestEntity.builder().testField(100).build()), XContentType.JSON));
        // fail : 1
        bulkProcessor.add(new IndexRequest(index, type).source("{\"testField\": \"aaa\"}"), XContentType.JSON);
        // success : 2
        bulkProcessor.add(new IndexRequest(index, type).source(mapper.writeValueAsBytes(BulkRequestEntity.builder().testField(200).build()), XContentType.JSON));
        // failure : 3
        bulkProcessor.add(new IndexRequest(index, type).source("{\"testField\": \"sss\"}"), XContentType.JSON);

        client.admin().indices().prepareRefresh(index).get();
        bulkProcessor.close();
    }
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Document(indexName = "bulk-request-test", type="test", shards = 1, replicas = 0, refreshInterval = "-1")
class BulkRequestEntity {
    @Id
    private String id;

    @Field(type = FieldType.Integer)
    private int testField;
}

