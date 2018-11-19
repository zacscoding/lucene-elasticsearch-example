package org.esdemo.rest.high;

import java.io.IOException;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;
import org.esdemo.rest.GeneralConstants;
import org.esdemo.util.GsonUtil;
import org.esdemo.util.SimpleLogger;
import org.junit.Test;

/**
 * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/5.6/java-rest-high-document-get.html
 *
 * @author zacconding
 * @Date 2018-05-04
 * @GitHub : https://github.com/zacscoding
 */
public class GetRequestTest {

    @Test
    public void defaultGet() throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(GeneralConstants.HOST_NAME, GeneralConstants.PORT,GeneralConstants.SCHEMA)));

        GetRequest getRequest = new GetRequest("persons", "doc", "180504-1111111");
        GetResponse getResponse = client.get(getRequest);

        /*
        {
          "_index": "persons",
          "_type": "doc",
          "_id": "180504-1111111",
          "_version": 4,
          "found": true,
          "_source": {
            "ssn": "180504-1111111",
            "name": "Zaccoding",
            "age": 1,
            "hobbies": [
              "Elastic Search",
              "coding"
            ]
          }
        }
         */
        System.out.println(GsonUtil.jsonStringToPretty(getResponse.toString()));

        /*
        ## index : persons
        ## type : doc
        ## id : persons
        ## version : 4
        ## sourceAsString : {"ssn":"180504-1111111","name":"Zaccoding","age":1,"hobbies":["Elastic Search","coding"]}
        ## sourceAsMap : {hobbies=[Elastic Search, coding], name=Zaccoding, age=1, ssn=180504-1111111}
        ## length of getSourceAsBytes : 89
         */
        SimpleLogger.build()
                    .appendln("## index : " + getResponse.getIndex())
                    .appendln("## type : " + getResponse.getType())
                    .appendln("## id : " + getResponse.getIndex())
                    .flush();

        if(getResponse.isExists()) {
            SimpleLogger.build()
                        .appendln("## version : " + getResponse.getVersion())
                        .appendln("## sourceAsString : " + getResponse.getSourceAsString())
                        .appendln("## sourceAsMap : " + getResponse.getSourceAsMap())
                        .appendln("## length of getSourceAsBytes : " + getResponse.getSourceAsBytes().length)
                        .flush();
        } else {
            System.out.println("# Not exist");
        }
    }

    @Test
    public void handleError() {
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));

        /*
        ## RestStatus.CONFLICT
         */
        try {
            GetRequest getRequest = new GetRequest("persons", "doc", "180504-1111111").version(2);
            GetResponse getResponse = client.get(getRequest);
            System.out.println(getResponse);
        } catch (ElasticsearchException e) {
            if(e.status() == RestStatus.CONFLICT) {
                System.out.println("## RestStatus.CONFLICT");
            } else {
                System.out.println(e.status());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        ## NOT FOUND EXCEPTION
         */
        try {
            GetRequest getRequest = new GetRequest("not_exist_index", "doc", "180504-1111111");
            GetResponse getResponse = client.get(getRequest);
            System.out.println(getResponse);
        } catch (ElasticsearchException e) {
            if(e.status() == RestStatus.NOT_FOUND) {
                System.out.println("## NOT FOUND EXCEPTION");
            } else {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
