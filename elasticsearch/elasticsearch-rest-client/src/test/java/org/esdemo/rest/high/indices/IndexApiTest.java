package org.esdemo.rest.high.indices;

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
 * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/5.6/java-rest-high-document-index.html
 *
 * @author zacconding
 * @Date 2018-05-04
 * @GitHub : https://github.com/zacscoding
 */
public class IndexApiTest {

    @Test
    public void indexPerson() throws Exception {
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(GeneralConstants.HOST_NAME, GeneralConstants.PORT,GeneralConstants.SCHEMA)));

        Person p = Person.builder().ssn("180504-1111111").name("Zaccoding").age(1).hobbies(Arrays.asList("Elastic Search", "coding")).build();

        ObjectMapper objectMapper = new ObjectMapper();
        IndexRequest request = new IndexRequest("persons", "doc", p.getSsn());

        String jsonString = objectMapper.writeValueAsString(p);

        request.source(jsonString);

        /*
        ## Response ##
        IndexResponse[index=persons,type=doc,id=180504-1111111,version=1,result=created,shards={"total":1,"successful":1,"failed":0}]
         */
        IndexResponse indexResponse = client.index(request);
        System.out.println("## Request ##");
        System.out.println(jsonString);
        System.out.println("## Response ##");
        System.out.println(indexResponse.toString());
    }
}
