package org.esdemo.rest.low;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.esdemo.rest.GeneralConstants;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-05-04
 * @GitHub : https://github.com/zacscoding
 */
public class InitializeTest {

    @Test
    public void init() {
        RestClient restClient = null;
        try {
            restClient = RestClient.builder(
                new HttpHost(GeneralConstants.HOST_NAME, GeneralConstants.PORT, GeneralConstants.SCHEMA)
            ).build();
        } finally {
            //restClient.close();
        }





    }


}
