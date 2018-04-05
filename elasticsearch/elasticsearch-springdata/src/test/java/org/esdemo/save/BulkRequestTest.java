package org.esdemo.save;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.esdemo.AbstractTestRunner;
import org.esdemo.dto.Counter;
import org.esdemo.util.SimpleLogger;
import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

/**
 * @author zacconding
 * @Date 2018-04-03
 * @GitHub : https://github.com/zacscoding
 */
public class BulkRequestTest extends AbstractTestRunner {
    @Test
    public void compareItemIndexAndListIndex() throws Exception {
        super.clearIndex(BulkRequestEntity.class);

        Counter counter = new Counter();
        BulkProcessor bulkProcessor = BulkProcessor.builder(client, new Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                // SimpleLogger.println("beforeBulk : {} / {}", executionId, request.numberOfActions());
            }
            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
//                SimpleLogger.println("afterBulk : {}, hasFailures : {}", executionId, response.hasFailures());
                int startIdx = counter.getValue();
                for (BulkItemResponse item : response.getItems()) {
                    int entityIdx = startIdx + item.getItemId();
//                    SimpleLogger.build().appendRepeat(20, "==").append("Result : " + item.isFailed()).appendRepeat(20, "==").newLine()
//                                .appendln(SimpleLogger.toJsonWithPretty(item))
//                                .appendRepeat(20, "====").newLine().flush();
                    SimpleLogger.println("expected list idx : {}, num of actions : {}, item id : {} , result : {}",entityIdx, request.numberOfActions(), item.getItemId(), item.isFailed());
                }
                counter.increaseAmount(request.numberOfActions());
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
        expected list idx : 0, num of actions : 4, item id : 0 , result : false
        expected list idx : 1, num of actions : 4, item id : 1 , result : true
        expected list idx : 2, num of actions : 4, item id : 2 , result : false
        expected list idx : 3, num of actions : 4, item id : 3 , result : true

        == afterBulk result if setBulkActions(2)
        expected list idx : 0, num of actions : 2, item id : 0 , result : false
        expected list idx : 1, num of actions : 2, item id : 1 , result : true
        expected list idx : 2, num of actions : 2, item id : 0 , result : false
        expected list idx : 3, num of actions : 2, item id : 1 , result : true
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

    @Test
    public void saveWithSomeTerms() throws Exception {
        super.clearIndex(BulkRequestEntity.class);

        BulkProcessor bulkProcessor = BulkProcessor.builder(client, new Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                SimpleLogger.info("[## Before bulk] execution id : {}, number of actions : {}", executionId, request.numberOfActions());
            }
            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                SimpleLogger.info("[## After bulk {}] execution id : {}, number of actions : {}, has fail : {}",
                    Thread.currentThread().getId() + Thread.currentThread().getName(), executionId, request.numberOfActions(), response.hasFailures());
                // client.admin().indices().prepareRefresh("bulk-request-test").get();
            }
            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                SimpleLogger.println("[## After bulk with throwable] execution id : {}, number of actions : {}, message : {}"
                    , executionId, request.numberOfActions(), failure.getMessage());
            }
        }).setBulkActions(3).setFlushInterval(new TimeValue(1L, TimeUnit.SECONDS))
                                                   .setConcurrentRequests(0)
                                                   .setBackoffPolicy(BackoffPolicy.noBackoff())
                                                   .build();

//        // want to process 1 request
//        SimpleLogger.println("add 1 request & sleep 2");
//        bulkProcessor.add(getIndexRequest());
//        TimeUnit.SECONDS.sleep(6L);
//
//        // want to process 2 request
//        SimpleLogger.println("add 2 request & sleep 2");
//        bulkProcessor.add(getIndexRequest());
//        bulkProcessor.add(getIndexRequest());
//        TimeUnit.SECONDS.sleep(3L);
//
//        // want to process 2 request
//        SimpleLogger.println("add 1 request & sleep 1");
//        bulkProcessor.add(getIndexRequest());
//        SimpleLogger.println("add 1 request & sleep 1");
//        TimeUnit.SECONDS.sleep(3L);
//        bulkProcessor.add(getIndexRequest());
//        TimeUnit.SECONDS.sleep(5L);

        bulkProcessor.add(getIndexRequest());
        bulkProcessor.add(getIndexRequest());
        // bulkProcessor.awaitClose(5, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(10L);
        SearchQuery query = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.matchAllQuery())
                    .withIndices("bulk-request-test")
                    .build();
        long count = elasticsearchTemplate.count(query);
        System.out.println("before refresh : " + count);
    }

    @Test
    public void test() {
        client.admin().indices().prepareRefresh("bulk-request-test").get();
    }

    private IndexRequest getIndexRequest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return new IndexRequest("bulk-request-test", "test").source(mapper.writeValueAsBytes(getEntity()), XContentType.JSON);
    }

    private BulkRequestEntity getEntity() {
        return BulkRequestEntity.builder().id("test").testField(new Random().nextInt(100)).build();
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

