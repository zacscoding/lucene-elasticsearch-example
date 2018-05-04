package org.esdemo.rest.high;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.esdemo.entity.Person;
import org.esdemo.rest.GeneralConstants;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-05-04
 * @GitHub : https://github.com/zacscoding
 */
public class DeleteRequestTest {

    @Test
    public void defaultTest() throws Exception {
        RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(new HttpHost(GeneralConstants.HOST_NAME, GeneralConstants.PORT,GeneralConstants.SCHEMA)).build());

        Person p = Person.builder().ssn("1").name("Zaccoding").age(1).hobbies(Arrays.asList("Elastic Search", "coding")).build();
        ObjectMapper objectMapper = new ObjectMapper();
        IndexRequest request = new IndexRequest("persons", "doc", p.getSsn());
        request.source(objectMapper.writeValueAsString(p));

        IndexResponse indexResponse = client.index(request);

    }

}
