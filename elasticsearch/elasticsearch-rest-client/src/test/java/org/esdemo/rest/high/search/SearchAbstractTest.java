package org.esdemo.rest.high.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.esdemo.rest.GeneralConstants;
import org.esdemo.util.LogLevelUtil;
import org.esdemo.util.SimpleLogger;
import org.junit.After;
import org.junit.Before;

/**
 * @author zacconding
 * @Date 2018-11-22
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
public abstract class SearchAbstractTest {

    RestHighLevelClient client;
    final String testPersonIndex = "test-person";
    final String testPersonType = "person";

    @Before
    public void setUp() {
        LogLevelUtil.setInfo();
        client = new RestHighLevelClient(RestClient.builder(new HttpHost(GeneralConstants.HOST_NAME, GeneralConstants.PORT, GeneralConstants.SCHEMA)));

        if (useBefore()) {
            deleteIndex();
            createIndex();
            saveDocuments();
            refresh();
        }

    }

    @After
    public void tearDown() {
        if (useAfter()) {
            deleteIndex();
        }
    }

    void createIndex() {
        log.info("## Try to create index : {}", testPersonIndex);
        try {
            CreateIndexRequest request = new CreateIndexRequest(testPersonIndex);
            String mapping = "{"
                            + "\"person\" : {"
                            + " \"properties\" : {"
                            + " \"age\"     : { \"type\" : \"long\" },"
                            + " \"name\"    : {\"type\" : \"keyword\" },"
                            + " \"hobbies\" : {\"type\" : \"text\"}"
                            + "   }"
                            + " }"
                            + "}";
            request.settings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 0));
            request.mapping(testPersonType, mapping, XContentType.JSON);


            CreateIndexResponse response = client.indices().create(request);
            log.info("## > Result : {}", response.isAcknowledged());
        } catch (IOException e) {
            log.warn("IOException occur");
            throw new RuntimeException(e);
        }
    }

    void deleteIndex() {
        try {
            log.info("## Try to delete index : {}", testPersonIndex);
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(testPersonIndex);
            DeleteIndexResponse deleteResponse = client.indices().delete(deleteIndexRequest);
            log.info("## > result : {} index : {}", deleteResponse.isAcknowledged(), testPersonIndex);
        } catch (ElasticsearchStatusException e) {
            if (e.status().getStatus() != 404) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void saveDocuments() {
        final ObjectMapper objectMapper = new ObjectMapper();
        IntStream.rangeClosed(1, documentSize()).forEach(i -> {
            SearchTestPerson person = createTestPersons().apply(i);
            try {
                IndexRequest request = new IndexRequest(testPersonIndex, testPersonType, person.getName());
                request.source(objectMapper.writeValueAsString(person), XContentType.JSON);
                IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            } catch (IOException e) {
                SimpleLogger.error("IOException occur : " + e.getMessage(), null);
            } catch (Exception e) {
                SimpleLogger.error("Failed to save person " + person.toString(), e);
            }
        });
    }

    void refresh() {
        RefreshRequest refreshRequest = new RefreshRequest(testPersonIndex);
        try {
            client.indices().refresh(refreshRequest);
        } catch (IOException e) {
            SimpleLogger.error("Failed to refresh", e);
        }
    }

    abstract boolean useBefore();

    abstract boolean useAfter();

    abstract Function<Integer, SearchTestPerson> createTestPersons();

    abstract int documentSize();

    @Getter
    @Setter
    protected static class SearchTestPerson {

        private int age;
        private String name;
        private String[] hobbies;
        private String intro;

        @Override
        public String toString() {
            return "SearchTestPerson{" + "age=" + age + ", name='" + name + '\'' + ", hobbies=" + Arrays.toString(hobbies) + '}';
        }
    }
}
