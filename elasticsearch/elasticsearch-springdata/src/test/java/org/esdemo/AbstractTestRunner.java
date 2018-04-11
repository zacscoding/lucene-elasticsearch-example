package org.esdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.util.Assert;
import java.util.List;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.esdemo.dto.Pair;
import org.esdemo.elastic.ElasticsearchTemplateService;
import org.esdemo.util.ReflectionUtil;
import org.esdemo.util.SimpleLogger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AbstractTestRunner {

    @Autowired
    protected ElasticsearchTemplateService elasticsearchTemplate;

    @Autowired
    protected Client client;

    @Autowired
    protected ObjectMapper objectMapper;

    //@Before
    public void setUp() {
    }

    protected void clearIndex(Class<?> clazz) {
        if (elasticsearchTemplate.indexExists(clazz)) {
            Assert.isTrue(elasticsearchTemplate.deleteIndex(clazz));
        }
        Assert.isTrue(elasticsearchTemplate.createIndex(clazz));
        Assert.isTrue(elasticsearchTemplate.putMapping(clazz));
        elasticsearchTemplate.refresh(clazz);
    }

    protected <T> boolean save(T inst) {
        if (inst == null) {
            return false;
        }

        ElasticsearchPersistentEntity<T> persistentEntity = elasticsearchTemplate.getPersistentEntityFor(inst.getClass());

        try {
            IndexResponse response = elasticsearchTemplate.getClient().prepareIndex(persistentEntity.getIndexName(), persistentEntity.getIndexType())
                                                          .setId(ReflectionUtil.getSpringDataId(inst))
                                                          .setSource(objectMapper.writeValueAsBytes(inst), XContentType.JSON).get();
            return response.status().getStatus() == 201;
        } catch (Exception e) {
            return false;
        }
    }

    protected <T> void bulkProcess(List<T> entities) {
        if (entities == null || entities.size() == 0) {
            return;
        }

        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-docs-bulk-processor.html
        BulkProcessor bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                SimpleLogger.build().appendRepeat(20, "===").newLine().appendln("beforeBulk.. id : {}", executionId)
                            .appendln("req::numberOfActions() : {}", request.numberOfActions()).flush();
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                SimpleLogger.build().appendRepeat(20, "===").newLine().appendln("afterBulk.. id : {}", executionId)
                            .appendln("  req::numberOfActions() : {}", request.numberOfActions()).appendln("  res::status() : {}", response.status())
                            .flush();
                if (response.hasFailures()) {
                    for (BulkItemResponse item : response.getItems()) {
                        SimpleLogger.printJSONPretty(item);
                        if (item.isFailed()) {

                        }
                    }
                }
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                SimpleLogger.build().appendRepeat(20, "===").newLine().appendln("afterBulk.. id : {}", executionId)
                            .appendln("req::numberOfActions() : {}", request.numberOfActions()).appendln("fail::message : {}", failure.getMessage())
                            .flush();
            }
        }).setBulkActions(entities.size()).setConcurrentRequests(0).setFlushInterval(TimeValue.timeValueSeconds(5L)).build();

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

        entities.forEach(entity -> {
            try {
                System.out.println(mapper.writeValueAsString(entity));
                bulkProcessor.add(
                    new IndexRequest(indexAndType.getFirst(), indexAndType.getSecond()).source(mapper.writeValueAsBytes(entity), XContentType.JSON));
            } catch (Exception e) {
                SimpleLogger.error("Failed to parse json", e);
            }
        });

        // Refresh indices
        client.admin().indices().prepareRefresh(indexAndType.getFirst()).get();
        bulkProcessor.flush();
        // bulkProcessor.awaitClose(5L, TimeUnit.SECONDS);
        // bulkProcessor.close();
        //bulkProcessor.close();
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
