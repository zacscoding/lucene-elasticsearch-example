package org.esdemo.rest.high;

import org.apache.http.HttpHost;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.esdemo.rest.GeneralConstants;
import org.esdemo.util.SimpleLogger;
import org.junit.Test;

/**
 * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/5.6/java-rest-high-main.html
 *
 * @author zacconding
 * @Date 2018-05-04
 * @GitHub : https://github.com/zacscoding
 */
public class InfoApi {

    @Test
    public void clusterInfo() throws Exception {
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(GeneralConstants.HOST_NAME, GeneralConstants.PORT,GeneralConstants.SCHEMA)).build());
        MainResponse response = client.info();

        /*
        Cluster Name : Cluster [test]
        Cluster UUID : sCnCR-5jRB27eKjrQ1Yn_w
        Node Name : GW2UnYx
        Version : 5.6.8
         */
        SimpleLogger.build()
                    .appendln("Cluster Name : " + response.getClusterName().toString())
                    .appendln("Cluster UUID : " + response.getClusterUuid())
                    .appendln("Node Name : " + response.getNodeName())
                    .appendln("Version : " + response.getVersion())
                    .flush();

    }

}
