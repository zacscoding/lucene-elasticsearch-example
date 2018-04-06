package org.esdemo.save;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkItemResponse.Failure;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.esdemo.AbstractTestRunner;
import org.esdemo.util.SimpleLogger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;

/**
 * @author zacconding
 * @Date 2018-04-07
 * @GitHub : https://github.com/zacscoding
 */
public class UpdateRequestTest extends AbstractTestRunner {

    String indexName;
    String type;

    @Before
    public void setUp() {
        super.clearIndex(UpdateRequestTestEntity.class);
        ElasticsearchPersistentEntity persistentEntity = elasticsearchTemplate.getPersistentEntityFor(UpdateRequestTestEntity.class);
        indexName = persistentEntity.getIndexName();
        type = persistentEntity.getIndexType();
    }

    @After
    public void tearDown() {
        elasticsearchTemplate.deleteIndex(UpdateRequestTestEntity.class);
    }

    @Test
    public void updateTest() {
        UpdateRequestTestEntity e1 = UpdateRequestTestEntity.builder().id("test1").name("first").age(1).build();
        assertTrue(super.save(e1));

        Map<String, Object> update = new HashMap<>();
        update.put("name", "second");

        UpdateQuery updateQuery = new UpdateQuery();
        updateQuery.setId("test1");
        updateQuery.setIndexName(indexName);
        updateQuery.setType(type);
        updateQuery.setClazz(UpdateRequestTestEntity.class);
        updateQuery.setUpdateRequest(new UpdateRequest().doc(update));

        UpdateResponse updateResponse = elasticsearchTemplate.update(updateQuery);
        SimpleLogger.printTitle("Update Response");
        SimpleLogger.printJSONPretty(updateResponse);
    }

    @Test
    public void updateOnlyOne() throws IOException {
        UpdateRequestTestEntity e1 = UpdateRequestTestEntity.builder().id("test1").name("name").age(1).build();

        assertTrue(super.save(e1));
        BulkProcessor bulkProcessor = BulkProcessor.builder(client, new Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                System.out.println("afterBulk ");
                for (BulkItemResponse res : response.getItems()) {
                    SimpleLogger.printTitle("Check Response : " + res.getItemId());
                    if (res.isFailed()) {
                        // exception : class org.elasticsearch.index.engine.VersionConflictEngineException, status : 409
                        SimpleLogger.println("exception : {}, status : {}", res.getFailure().getCause().getClass(), res.getFailure().getStatus().getStatus());
                    } else {
                        SimpleLogger.printJSONPretty(res);
                    }
                }
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                System.out.println("afterBulk with throwable");
            }
        }).setBulkActions(3).setFlushInterval(new TimeValue(1L, TimeUnit.SECONDS)).setConcurrentRequests(0).build();
        bulkProcessor.add(new UpdateRequest().index(indexName).type(type).id("test1")
                                             .doc(XContentBuilder.builder(XContentType.JSON.xContent()).startObject().field("name", "name2").endObject()).version(1L));
        bulkProcessor.add(new UpdateRequest().index(indexName).type(type).id("test1")
                                             .doc(XContentBuilder.builder(XContentType.JSON.xContent()).startObject().field("name", "name3").endObject()).version(1L));
        bulkProcessor.add(new UpdateRequest().index(indexName).type(type).id("test1")
                                             .doc(XContentBuilder.builder(XContentType.JSON.xContent()).startObject().field("name", "name4").endObject()).version(1L));

        client.admin().indices().prepareRefresh(indexName).get();
        bulkProcessor.flush();

        GetQuery getQuery = new GetQuery();
        getQuery.setId("test1");
        UpdateRequestTestEntity entity = elasticsearchTemplate.queryForObject(getQuery, UpdateRequestTestEntity.class);
        assertThat(entity.getName(), is("name2"));
    }
}


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Document(indexName = "update-request-test", type = "test", shards = 1, replicas = 0, refreshInterval = "-1")
class UpdateRequestTestEntity {

    @Id
    private String id;

    @Field(type = FieldType.keyword)
    private String name;

    @Field(type = FieldType.Integer)
    private int age;
}