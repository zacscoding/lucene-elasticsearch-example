package org.esdemo.configuration;

import java.io.IOException;
import javax.annotation.PostConstruct;
import org.apache.http.HttpHost;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.esdemo.util.SimpleLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author zacconding
 * @Date 2018-05-04
 * @GitHub : https://github.com/zacscoding
 */

@Configuration
public class CheckClusterConfiguration {

    @Value("${es.hostName}")
    private String hostName;
    @Value("${es.port}")
    private int port;
    @Value("${es.schema}")
    private String schema;

    @PostConstruct
    public void init() {
        highLevelClientCheck();
    }

    private void highLevelClientCheck() {
        try {
            SimpleLogger.println("## Try to connect ==>  host : {}, port : {}, schema : {}", hostName, port, schema);
            RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(hostName, port, schema)));
            MainResponse response = client.info();

            SimpleLogger.build()
                        .appendln("### Elasticsearch Cluster Connection Test ###")
                        .appendln("Cluster Name : " + response.getClusterName().toString())
                        .appendln("Cluster UUID : " + response.getClusterUuid())
                        .appendln("Node Name : " + response.getNodeName())
                        .appendln("Version : " + response.getVersion())
                        .appendln("#############################################")
                        .flush();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
