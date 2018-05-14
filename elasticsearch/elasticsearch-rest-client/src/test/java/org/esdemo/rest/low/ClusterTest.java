package org.esdemo.rest.low;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StringUtils;

/**
 * @author zacconding
 * @Date 2018-05-14
 * @GitHub : https://github.com/zacscoding
 */
public class ClusterTest {

    private RestClient restClient;

    @Before
    public void setUp() {
        restClient = RestClient.builder(
            new HttpHost("192.168.5.50", 9200, "http")
        ).build();
    }

    @Test
    public void notAsync() {
        waitForState("green", "berith-blocks", "300s");
    }

    private void waitForState(String state, String indexName, String timeout) {
        try {
            Map<String, String> param = new HashMap<>();
            param.put("wait_for_status", state);

            if (StringUtils.hasText(indexName)) {
                param.put("index", indexName);
            }

            if (StringUtils.hasText(timeout)) {
                param.put("timeout", timeout);
            }

            restClient.performRequest("GET", "/_cluster/health", param);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
