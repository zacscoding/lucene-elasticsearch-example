package org.esdemo.save;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteRequest.OpType;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkItemResponse.Failure;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.esdemo.AbstractTestRunner;
import org.esdemo.dto.Counter;
import org.esdemo.util.SimpleLogger;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.filter.TypeExcludeFilters;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.query.GetQuery;
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
    public void sameIdAndCatchException() throws Exception {
        BulkProcessor bulkProcessor = BulkProcessor.builder(client, new Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                SimpleLogger.info("[## Before bulk] execution id : {}, number of actions : {}", executionId, request.numberOfActions());
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                SimpleLogger.info("[## After bulk] execution id : {}, number of actions : {}, has fail : {}",
                    executionId, request.numberOfActions(), response.hasFailures());
                for (BulkItemResponse item : response.getItems()) {
                    if (item.isFailed()) {
                        // ## check fail
                        Failure failure =  item.getFailure();
                        SimpleLogger.build()
                                    .appendln("id : " + failure.getId())
                                    .appendln("message : " + failure.getMessage())
                                    .appendln("status name : " + failure.getStatus().name())
                                    .appendln("status : " + failure.getStatus().getStatus())
                                    .appendln("cause class : " + failure.getCause().getClass().getName())
                                    .flush();
                        SimpleLogger.println("", failure.getId());
                        SimpleLogger.printJSONPretty(item.getFailure());
                        // System.out.println("## check fail : " + item.getFailure().getCause().getClass().getName());
                        // SimpleLogger.printJSONPretty(item);
                    }
                }
            }
            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                SimpleLogger.println("[## After bulk with throwable] execution id : {}, number of actions : {}, message : {}"
                    , executionId, request.numberOfActions(), failure.getMessage());
                System.out.println(failure.getClass().getName());
            }
        }).setBulkActions(3).setFlushInterval(new TimeValue(1L, TimeUnit.SECONDS)).setConcurrentRequests(0).build();

        /**     Will be updated testField : 2 -> 3   */

//        super.clearIndex(BulkRequestEntity.class);
//        bulkProcessor.add(getIndexRequest(BulkRequestEntity.builder().id("1").testField(1).build()));
//        bulkProcessor.add(getIndexRequest(BulkRequestEntity.builder().id("2").testField(2).build()));
//        bulkProcessor.add(getIndexRequest(BulkRequestEntity.builder().id("2").testField(3).build()));
//        client.admin().indices().prepareRefresh("bulk-request-test").get();
//        bulkProcessor.flush();
//        TimeUnit.SECONDS.sleep(2L);
//
//        BulkRequestEntity e1 = findOneById("2");
//        assertTrue(e1.getTestField() == 3);

        // ===============================================================================================================

        super.clearIndex(BulkRequestEntity.class);
        bulkProcessor.add(getIndexRequest(BulkRequestEntity.builder().id("1").testField(1).build()).opType(OpType.CREATE));
        bulkProcessor.add(getIndexRequest(BulkRequestEntity.builder().id("2").testField(2).build()).opType(OpType.CREATE));
        bulkProcessor.add(getIndexRequest(BulkRequestEntity.builder().id("2").testField(3).build()).opType(OpType.CREATE));
        client.admin().indices().prepareRefresh("bulk-request-test").get();
        bulkProcessor.flush();
        TimeUnit.SECONDS.sleep(1L);
        BulkRequestEntity e2 = findOneById("2");
        assertTrue(e2.getTestField() == 2);
    }

    @Test
    public void updateButNotExistDocu() throws Exception {
        BulkProcessor bulkProcessor = BulkProcessor.builder(client, new Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {}

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                List<Object> payloads = request.payloads();
                if(payloads == null) {
                    System.out.println("payloads is null");
                } else {{
                    System.out.println("payloads size : " + payloads.size());
                }}
                if (response.hasFailures()) {
                    for (BulkItemResponse itemResponse : response.getItems()) {
                        if (itemResponse.isFailed()) {
                            SimpleLogger.println("## index : {}, type : {}, id : {}, status : {}",
                                itemResponse.getIndex(), itemResponse.getType(), itemResponse.getId(), itemResponse.status().getStatus());
                        }
                    }
                }
            }
            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                SimpleLogger.println("## after bulk with exception");
            }
        }).setBulkActions(1).setFlushInterval(new TimeValue(1L, TimeUnit.SECONDS)).setConcurrentRequests(0).build();

        super.clearIndex(BulkRequestEntity.class);

        final String index = "bulk-request-test";
        final String type = "test";

        UpdateRequest updateRequest = new UpdateRequest(index, type, "test");
        Map<String,Object> param = new HashMap<>();
        param.put("testField", 10);
        updateRequest.doc(param);

        bulkProcessor.add(updateRequest);
        client.admin().indices().prepareRefresh("bulk-request-test").get();
        bulkProcessor.flush();
        TimeUnit.SECONDS.sleep(1L);
    }

    public BulkRequestEntity findOneById(String id) {
        GetQuery getQuery = new GetQuery();
        getQuery.setId(id);
        return elasticsearchTemplate.queryForObject(getQuery , BulkRequestEntity.class);
    }

    @Test
    @Ignore
    public void testSearch() {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(new IdsQueryBuilder().addIds("2","3"))
            .build();
        Page<BulkRequestEntity> pages = elasticsearchTemplate.queryForPage(searchQuery, BulkRequestEntity.class);
        System.out.println(pages.getTotalElements());
    }

    @Test
    public void checkListener() throws Exception {
        super.clearIndex(BulkRequestEntity.class);

        BulkProcessor bulkProcessor = BulkProcessor.builder(client, new Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
            }
            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                for (BulkItemResponse item : response.getItems()) {
                    DocWriteRequest req = request.requests().get(item.getItemId());
                    SimpleLogger.println("class : {}, index : {}, type : {}, id : {}",
                        req.getClass().getName(), req.index(), req.type(), req.id());
                }
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
            }
        }).setBulkActions(1).setConcurrentRequests(0).build();
        ObjectMapper mapper = new ObjectMapper();
        final String index = "bulk-request-test";
        final String type = "test";
        bulkProcessor.add(new IndexRequest(index, type).source(mapper.writeValueAsBytes(BulkRequestEntity.builder().testField(100).build()), XContentType.JSON));
        client.admin().indices().prepareRefresh(index).get();
        bulkProcessor.close();
    }

    private IndexRequest getIndexRequest() throws Exception {
        return getIndexRequest(getEntity());
    }

    private IndexRequest getIndexRequest(BulkRequestEntity e) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String id = e.getId();
        e.setId(null);
        return new IndexRequest("bulk-request-test", "test").id(id).source(mapper.writeValueAsBytes(e), XContentType.JSON);
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
@Document(indexName = "bulk-request-test", type="test", shards = 1, replicas = 0, refreshInterval = "1s")
@JsonIgnoreProperties({"id"})
class BulkRequestEntity {
    @Id
    private String id;

    @Field(type = FieldType.Integer)
    private int testField;
}