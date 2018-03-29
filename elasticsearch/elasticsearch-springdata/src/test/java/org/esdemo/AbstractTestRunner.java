package org.esdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.esdemo.dto.Pair;
import org.esdemo.entity.ReceiptEntity;
import org.esdemo.repository.*;
import org.esdemo.util.SimpleLogger;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AbstractTestRunner {

    @Autowired
    protected ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    protected Client client;

    @Before
    public void setUp() {

    }

    protected void clearIndex(Class<?> clazz) {
        elasticsearchTemplate.deleteIndex(clazz);
        elasticsearchTemplate.createIndex(clazz);
        elasticsearchTemplate.putMapping(clazz);
        elasticsearchTemplate.refresh(clazz);
    }

    protected <T> void bulkProcess(List<T> entities) {
        if (entities == null || entities.size() == 0) {
            return;
        }

        BulkProcessor bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                SimpleLogger.info("@@ before bulk execution id : {}, request : {}", executionId, request);
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                SimpleLogger.info("@@ before bulk execution id : {}, request : {}", executionId, request);
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                SimpleLogger.error("@@ failed to bulk", failure);
            }
        }).setBulkActions(1000).setConcurrentRequests(0).build();

        Class<?> clazz = entities.get(0).getClass();
        Pair<String, String> indexAndType = getIndexAndType(clazz);

        if (!StringUtils.hasText(indexAndType.getFirst())) {
            SimpleLogger.error("Not exist index name", null);
            return;
        }

        if (!StringUtils.hasText(indexAndType.getSecond())) {
            indexAndType.setSecond(clazz.getSimpleName().toLowerCase());
        }

        SimpleLogger.info("@@ {}` Index name : {}, type : {}", clazz.getSimpleName(), indexAndType.getFirst(), indexAndType.getSecond());

        ObjectMapper mapper = new ObjectMapper();

        entities.forEach(e -> {
            try {
                System.out.println(mapper.writeValueAsString(e));
                bulkProcessor.add(new IndexRequest(indexAndType.getFirst(), indexAndType.getSecond()).source(mapper.writeValueAsBytes(e), XContentType.JSON));
            } catch (Exception e2) {
                SimpleLogger.error("Failed to parse json", e2);
            }
        });

        // Refresh indices
        client.admin().indices().prepareRefresh(indexAndType.getFirst()).get();

        bulkProcessor.flush();
//        //bulkProcessor.close();
//        SimpleLogger.info("Start to await close..");
//        try {
//            bulkProcessor.awaitClose(5L, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            bulkProcessor.close();
//        }
//        SimpleLogger.info("End bulk process");
    }

    private Pair<String, String> getIndexAndType(Class<?> clazz) {
        try {
            Document doc = clazz.getAnnotation(Document.class);
            return new Pair<>(doc.indexName(), doc.type());
        } catch (Exception e) {
            return null;
        }
    }

}
