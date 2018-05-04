package org.esdemo.jest;

import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import java.io.IOException;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilders;
import org.esdemo.util.LogLevelUtil;
import org.esdemo.util.SimpleLogger;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-05-04
 * @GitHub : https://github.com/zacscoding
 */
public class ConnectionTest extends JestTestRunner {

    @Test
    public void createIndexAndSettings() {
        try {
            LogLevelUtil.setInfo();
            String indexName = "persons";

            JestResult deleteResult = client.execute(new DeleteIndex.Builder(indexName).build());
            SimpleLogger.build()
                        .appendln("## Delete Index : {} ##", indexName)
                        .appendln("## Result Json String : " + deleteResult.getJsonString())
                        .appendln("## Response code : " + deleteResult.getResponseCode())
                        .flush();


            Settings.Builder settingsBuilder = Settings.builder();
            settingsBuilder.put("number_of_shards", 5);
            settingsBuilder.put("number_of_replicas", 0);
            settingsBuilder.put("refresh_interval", "1s");

            JestResult result = client.execute(new CreateIndex.Builder("persons").settings(settingsBuilder.build().getAsMap()).build());

            SimpleLogger.build()
                        .appendln("## Create Index : {} ##", indexName)
                        .appendln("## Result Json String : " + result.getJsonString())
                        .appendln("## Response code : " + result.getResponseCode())
                        .flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
