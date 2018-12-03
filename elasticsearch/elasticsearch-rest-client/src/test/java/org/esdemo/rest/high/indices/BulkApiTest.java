package org.esdemo.rest.high.indices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.esdemo.entity.Person;
import org.esdemo.rest.GeneralConstants;
import org.esdemo.util.SimpleLogger;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-12-03
 * @GitHub : https://github.com/zacscoding
 */
public class BulkApiTest {

    private ObjectMapper objectMapper = new ObjectMapper();
    private RestHighLevelClient client;

    @Before
    public void setUp() {
        client = new RestHighLevelClient(RestClient.builder(new HttpHost(GeneralConstants.HOST_NAME, GeneralConstants.PORT, GeneralConstants.SCHEMA)));
    }

    @Test
    public void bulkRequest() throws IOException {
        BulkRequest request = new BulkRequest();

        IntStream.rangeClosed(1, 10).forEach(i -> {
            try {
                IndexRequest indexRequest = new IndexRequest("person", "doc", String.valueOf(i));
                BulkTestPerson person = new BulkTestPerson(i, "hiva" + i, Arrays.asList("coding"));
                indexRequest.source(objectMapper.writeValueAsString(person), XContentType.JSON);
                request.add(indexRequest);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

        request.timeout(TimeValue.timeValueMinutes(2L));
        request.setRefreshPolicy(RefreshPolicy.WAIT_UNTIL);


        BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
        for (BulkItemResponse response : bulkResponse.getItems()) {
            SimpleLogger.println("{} > {}", response.getId(), response.isFailed());
        }
    }


    @Getter
    @Setter
    @AllArgsConstructor
    public static class BulkTestPerson {

        private int age;
        private String name;
        private List<String> hobbies;

    }


}
