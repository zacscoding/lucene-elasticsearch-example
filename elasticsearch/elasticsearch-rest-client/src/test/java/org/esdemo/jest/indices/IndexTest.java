package org.esdemo.jest.indices;

import io.searchbox.client.JestResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.IndicesExists;
import io.searchbox.indices.mapping.PutMapping;
import java.io.IOException;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.mapper.LegacyIntegerFieldMapper;
import org.elasticsearch.index.mapper.RootObjectMapper;
import org.elasticsearch.index.mapper.StringFieldMapper;
import org.elasticsearch.index.mapper.TextFieldMapper;
import org.esdemo.jest.JestTestRunner;
import org.esdemo.util.GsonUtil;
import org.esdemo.util.LogLevelUtil;
import org.esdemo.util.SimpleLogger;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-05-04
 * @GitHub : https://github.com/zacscoding
 */
public class IndexTest extends JestTestRunner {

    @Test
    public void createIndexAndSettings() {
        try {
            LogLevelUtil.setInfo();
            String indexName = "persons";

            /**     Delete Index        */

            /*
            ## Delete Index : persons , result : true ##
            {
              "acknowledged": true
            }
            ============================================================================= ##
             */
            System.out.println("## Client : " + client);
            JestResult deleteResult = client.execute(new DeleteIndex.Builder(indexName).build());
            SimpleLogger.build()
                        .appendln("## Delete Index : {} , result : {} ##", indexName, deleteResult.isSucceeded())
                        .appendln(GsonUtil.jsonStringToPretty(deleteResult.getJsonString()))
                        .appendln("============================================================================= ##")
                        .flush();


            Settings.Builder settingsBuilder = Settings.builder();
            settingsBuilder.put("number_of_shards", 5);
            settingsBuilder.put("number_of_replicas", 0);
            settingsBuilder.put("refresh_interval", "1s");

            /**     Create Index        */
            /*
            ## Create Index : persons , result : true , response : 200 ##
            {
              "acknowledged": true,
              "shards_acknowledged": true,
              "index": "persons"
            }
            ============================================================================= ##
             */
            System.out.println("## Client : " + client);
            JestResult result = client.execute(new CreateIndex.Builder(indexName).settings(settingsBuilder.build().getAsMap()).build());
            SimpleLogger.build()
                        .appendln("## Create Index : {} , result : {} , response : {} ##", indexName, result.isSucceeded(), result.getResponseCode())
                        .appendln(GsonUtil.jsonStringToPretty(result.getJsonString()))
                        .appendln("============================================================================= ##")
                        .flush();

            /**     Index Exist        */
            /*
            ## Check index exist : persons ==> true
            ## Check index exist : persons_nope ==> false
            ============================================================================= ##
             */
            IndicesExists indicesExists = new IndicesExists.Builder(indexName).build();
            System.out.println("## Client : " + client);
            JestResult indexExistResult = client.execute(indicesExists);
            SimpleLogger.build()
                        .appendln("## Check index exist : {} ==> {}", indexName, indexExistResult.isSucceeded())
                        .flush();

            System.out.println("## Client : " + client);
            indexExistResult = client.execute(new IndicesExists.Builder(indexName+"_nope").build());
            SimpleLogger.build()
                        .appendln("## Check index exist : {} ==> {}", indexName+"_nope", indexExistResult.isSucceeded())
                        .appendln("============================================================================= ##")
                        .flush();


            /**     Create Index Twice        */
            /*
            ## Create Index Twice : persons , result : false , response : 400 ##
            {
              "error": {
                "root_cause": [
                  {
                    "type": "index_already_exists_exception",
                    "reason": "index [persons/R9U9uUyuSZ-Sv9sVDo6HLQ] already exists",
                    "index_uuid": "R9U9uUyuSZ-Sv9sVDo6HLQ",
                    "index": "persons"
                  }
                ],
                "type": "index_already_exists_exception",
                "reason": "index [persons/R9U9uUyuSZ-Sv9sVDo6HLQ] already exists",
                "index_uuid": "R9U9uUyuSZ-Sv9sVDo6HLQ",
                "index": "persons"
              },
              "status": 400
            }
            ============================================================================= ##
             */
            // request twice
            System.out.println("## Client : " + client);
            result = client.execute(new CreateIndex.Builder("persons").settings(settingsBuilder.build().getAsMap()).build());
            SimpleLogger.build()
                        .appendln("## Create Index Twice : {} , result : {} , response : {} ##", indexName, result.isSucceeded(), result.getResponseCode())
                        .appendln(GsonUtil.jsonStringToPretty(result.getJsonString()))
                        .appendln("============================================================================= ##")
                        .flush();

            /**     Put mapping       *//*
            RootObjectMapper.Builder rootObjectMapperBuilder = new RootObjectMapper.Builder("type")
                .add(new StringFieldMapper.Builder("ssn").index(true))
                .add(new StringFieldMapper.Builder("name").index(true))
                .add(new LegacyIntegerFieldMapper.Builder("age").index(true))
                .add(new TextFieldMapper.Builder("hobbies").index(true));

            rootObjectMapperBuilder.toString();


            PutMapping putMapping = new PutMapping.Builder(indexName, "type", rootObjectMapperBuilder).build();
            JestResult mappingResult = client.execute(putMapping);
            SimpleLogger.build()
                        .appendln("## Put mapping index : {}, type : {}, result : {}", indexName, "type", mappingResult.isSucceeded())
                        .appendln("============================================================================= ##")
                        .flush();*/

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void mapping() {
        RootObjectMapper.Builder rootObjectMapperBuilder = new RootObjectMapper.Builder("type")
            .add(new StringFieldMapper.Builder("ssn").index(true))
            .add(new StringFieldMapper.Builder("name").index(true))
            .add(new LegacyIntegerFieldMapper.Builder("age").index(true))
            .add(new TextFieldMapper.Builder("hobbies").index(true));
        System.out.println(rootObjectMapperBuilder.toString());
    }
}
