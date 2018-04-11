package org.esdemo.save;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.media.jfxmedia.logging.Logger;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.elasticsearch.action.DocWriteRequest.OpType;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.esdemo.AbstractTestRunner;
import org.esdemo.elastic.bulk.BulkItemResponseConsumer;
import org.esdemo.elastic.bulk.IndexRequestWrapper;
import org.esdemo.service.BulkProcessServiceWithSingleton;
import org.esdemo.util.SimpleLogger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * TEST ::  Handle bulk response respectively
 *
 * @author zacconding
 * @Date 2018-04-11
 * @GitHub : https://github.com/zacscoding
 */
public class BulkWrappingTest extends AbstractTestRunner {

    @Autowired
    private BulkProcessServiceWithSingleton bulkService;

    String index, type;

    @Before
    public void setUp() {
        super.clearIndex(BulkWrappingTestEntity.class);
        index = "bulk-wrapping-test";
        type = "test";
    }

    @Test
    public void requestBulkAndHandleRespectively() throws Exception {
        System.out.println("## request bulk and handle respectively");
        try {
            BulkItemResponseConsumer bulkIndexSuccessConsumer = (res, inst) -> {
                SimpleLogger.printTitle("Handle Success Response");
                SimpleLogger.println("status : {}, instance : {}",
                    res.status().getStatus(), inst);
            };

            BulkItemResponseConsumer bulkIndexFailConsumer = (res, inst) -> {
                SimpleLogger.printTitle("Handle Fail Response");
                SimpleLogger.println("status : {}, instance : {}",
                    res.status().getStatus(), inst);
            };

            BulkWrappingTestEntity e1 = BulkWrappingTestEntity.builder().id("id1").testField("willsuccess").build();
            BulkWrappingTestEntity e2 = BulkWrappingTestEntity.builder().id("id1").testField("willfail").build();
            BulkWrappingTestEntity e3 = BulkWrappingTestEntity.builder().id("id2").testField("willsuccess").build();

            bulkService.add(getIndexRequestWrapper(e1,bulkIndexSuccessConsumer, bulkIndexFailConsumer));
            bulkService.add(getIndexRequestWrapper(e2,bulkIndexSuccessConsumer, bulkIndexFailConsumer));
            bulkService.add(getIndexRequestWrapper(e3,bulkIndexSuccessConsumer, bulkIndexFailConsumer));
            client.admin().indices().prepareRefresh(index).get();

            TimeUnit.SECONDS.sleep(5L);
            /*
            ## request bulk and handle respectively
            ## ======================  [1, main] : Before bulk : 1  ======================##
            ## ======================  [37, elasticsearch[_client_][listener][T#1]] : After bulk : 1  ======================##
            ## Accept response
            ## ======================  Handle Success Response  ======================##
            status : 201, instance : BulkWrappingTestEntity(id=id1, testField=willsuccess)
            ## Accept response
            ## ======================  Handle Fail Response  ======================##
            status : 409, instance : BulkWrappingTestEntity(id=id1, testField=willfail)
            ## Accept response
            ## ======================  Handle Success Response  ======================##
            status : 201, instance : BulkWrappingTestEntity(id=id2, testField=willsuccess)
             */
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private IndexRequestWrapper getIndexRequestWrapper(BulkWrappingTestEntity e, BulkItemResponseConsumer successConsumer, BulkItemResponseConsumer failConsumer) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        IndexRequestWrapper r = new IndexRequestWrapper(index, type, e.getId());
        r.source(mapper.writeValueAsBytes(e), XContentType.JSON);
        r.opType(OpType.CREATE);
        r.setRequestInstance(e);
        r.setSuccessConsumer(successConsumer);
        r.setFailConsumer(failConsumer);
        return r;
    }
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Document(indexName = "bulk-wrapping-test", type = "test", shards = 1, replicas = 0, refreshInterval = "1s")
class BulkWrappingTestEntity {
    @Id
    private String id;

    @Field(type = FieldType.keyword)
    private String testField;
}