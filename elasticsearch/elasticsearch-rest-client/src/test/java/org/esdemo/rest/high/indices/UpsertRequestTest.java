package org.esdemo.rest.high.indices;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpHost;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.esdemo.rest.GeneralConstants;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-12-11
 * @GitHub : https://github.com/zacscoding
 */
public class UpsertRequestTest {

    RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(new HttpHost(GeneralConstants.HOST_NAME, GeneralConstants.PORT, GeneralConstants.SCHEMA))
    );

    @Test
    public void upsertTest() throws Exception {
        UpdateRequest updateRequest = new UpdateRequest("test", "_doc", "hivava");
        try(XContentBuilder builder = XContentFactory.jsonBuilder()) {
            builder.startObject()
                        .field("name", "hivava")
                        .field("age", 222)
                   .endObject();
            updateRequest.doc(builder);

            UpdateResponse response = client.update(updateRequest.docAsUpsert(true), RequestOptions.DEFAULT);
            System.out.println(response.status());
        }
    }

    /*
    {
      "scripted_upsert":true,
      "script": {
        "lang": "painless",
        "inline" : "if (ctx._source.time == null) { ctx._source.time = params.time } ctx._source.balance=params.balance",
        "params": {
          "time" : 5000,
          "balance" : "4000"
        }
      },
      "upsert": {
          "addr"	 : "aa",
          "balance" : "4000"
       }
    }
     */
    @Test
    public void upsertWithScript() throws Exception {
        String addr = "addr11";
        String balance = "100000";
        long time = 10000L;

        UpdateRequest updateRequest = new UpdateRequest("test", "_doc", "hivava");

        // script
        String scriptString = "if (ctx._source.time == null) { ctx._source.time = params.time } ctx._source.balance=params.balance";
        Map<String, Object> param = new HashMap<>();
        param.put("time", time);
        param.put("balance", balance);
        Script script = new Script(ScriptType.INLINE, "painless", scriptString, param);
        updateRequest.script(script);

        // upsert
        try(XContentBuilder builder = XContentFactory.jsonBuilder()) {
            builder.startObject()
                   .field("addr",addr)
                   .field("balance", balance)
                   .endObject();
            updateRequest.upsert(builder);

            UpdateResponse response = client.update(updateRequest.scriptedUpsert(true), RequestOptions.DEFAULT);
            System.out.println("## >> response : " + response.status());
        }
    }
}
