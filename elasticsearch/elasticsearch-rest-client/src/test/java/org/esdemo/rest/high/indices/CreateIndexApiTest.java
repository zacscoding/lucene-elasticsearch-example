package org.esdemo.rest.high.indices;

import java.io.IOException;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.esdemo.rest.GeneralConstants;
import org.esdemo.util.SimpleLogger;
import org.junit.Before;
import org.junit.Test;

/**
 * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.2/java-rest-high-create-index.html
 *
 * @author zacconding
 * @Date 2018-11-19
 * @GitHub : https://github.com/zacscoding
 */
public class CreateIndexApiTest {

    private RestHighLevelClient client;

    @Before
    public void setUp() throws IOException {
        client = new RestHighLevelClient(
            RestClient.builder(new HttpHost(GeneralConstants.HOST_NAME, GeneralConstants.PORT,GeneralConstants.SCHEMA))
        );

        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("twitter");
        try {
            client.indices().delete(deleteIndexRequest);
        } catch(ElasticsearchStatusException e) {
            // 404 if not exist
            System.out.println(e.status().getStatus());
        }
    }

    @Test
    public void indexSettings() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("twitter");

        // settings
        request.settings(
            Settings.builder()
                    .put("index.number_of_shards", 3)
                    .put("index.number_of_replicas", 2)
        );

        // mappings
        request.mapping("tweet",
            "  {\n" +
                "    \"tweet\": {\n" +
                "      \"properties\": {\n" +
                "        \"message\": {\n" +
                "          \"type\": \"text\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }", XContentType.JSON);


        CreateIndexResponse response = client.indices().create(request);
        SimpleLogger.build()
                    .appendln("## index() : " +  response.index())
                    .appendln("## isShardsAcknowledged() : " + response.isShardsAcknowledged())
                    .flush();

        GetIndexRequest getIndexRequest = new GetIndexRequest();
        getIndexRequest.indices("twitter");
        boolean exist = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println(">> Exist check : " + exist);

        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("twitter");
        DeleteIndexResponse deleteResponse = client.indices().delete(deleteIndexRequest);

        SimpleLogger.build()
                    .appendln("## isAcknowledged() : " + deleteResponse.isAcknowledged())
                    .flush();
    }
}
