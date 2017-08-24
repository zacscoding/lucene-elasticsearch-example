package com.elasticsearch_java.api.document;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.rest.RestStatus;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.elasticsearch_java.app.ClientFactory;
import com.elasticsearch_java.entity.SampleEntity;
import com.elasticsearch_java.util.DateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class IndexApiTest {
    private static TransportClient client;
    private String indexName;
    private String type;
    private boolean display;
    ObjectMapper mapper;

    @Before
    public void setUp(){
        indexName = "sample";
        type = "entity";
        display = false;
        mapper = new ObjectMapper();
        try {
            DeleteIndexResponse deleteResponse =
                    client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
        } catch (Exception e) {
            // ignore
        }
        CreateIndexResponse createResponse =
                client.admin().indices().create(new CreateIndexRequest(indexName)).actionGet();
    }

    @BeforeClass
    public static void testSetUp(){
        client = ClientFactory.getClient();
    }

    @AfterClass
    public static void testTearDown(){
        if (client != null)
            client.close();
    }

    @Test
    public void saveAndGet() throws Exception{
        SampleEntity entity = new SampleEntity();
        entity.setId("1");
        entity.setName("entity1");
        entity.setRegDate(new Date());
        entity.setAge(10);

        // save
        // IndexResponse response =
        // client.prepareIndex("sample","entity",entity.getId()).setSource(mapper.writeValueAsString(entity)).get();
        IndexResponse response = client.prepareIndex(indexName, type, entity.getId())
                .setSource(mapper.writeValueAsString(entity), XContentType.JSON).get();

        // result
        String _index = response.getIndex();
        String _type = response.getType();
        String _id = response.getId();
        long _version = response.getVersion();
        RestStatus status = response.status();

        if (display) {
            System.out.println("index : " + _index);
            System.out.println("_type : " + _type);
            System.out.println("_id : " + _id);
            System.out.println("_version : " + _version);
            System.out.println("status_ : " + status.getStatus());
        }

        GetResponse getResponse = client.prepareGet(indexName, type, entity.getId()).setOperationThreaded(false).get();
        if (display) {
            getResponse.getSource().forEach((k, v) -> {
                System.out.println("key : " + k + ", value : " + v);
            });
        }
    }

    @Test
    public void delete() throws JsonProcessingException{
        SampleEntity entity = new SampleEntity();
        entity.setId("1");
        entity.setName("entity1");
        entity.setRegDate(new Date());
        entity.setAge(10);

        ObjectMapper mapper = new ObjectMapper();

        // save
        // IndexResponse response =
        // client.prepareIndex("sample","entity",entity.getId()).setSource(mapper.writeValueAsString(entity)).get();
        IndexResponse response = client.prepareIndex(indexName, type, entity.getId())
                .setSource(mapper.writeValueAsString(entity), XContentType.JSON).get();
        assertTrue(response.status().getStatus() == 201);

        // delete
        DeleteResponse deleteResponse = client.prepareDelete(indexName, type, entity.getId()).get();
        assertThat(deleteResponse.status().name(), is("OK"));
        GetResponse getResponse = client.prepareGet(indexName, type, entity.getId()).setOperationThreaded(false).get();
        assertNull(getResponse.getSource());
    }

    @Test
    public void deleteByQuery() throws JsonProcessingException{
        // https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-docs-delete-by-query.html
        // given
        SampleEntity e1 = new SampleEntity("1", "entity1", 10, new Date());
        SampleEntity e2 = new SampleEntity("2", "entity2", 10, new Date());
        SampleEntity e3 = new SampleEntity("3", "entity3", 20, new Date());

        BulkRequestBuilder bulkRequest = client.prepareBulk();
        bulkRequest.add(client.prepareIndex(indexName, type, e1.getId()).setSource(mapper.writeValueAsString(e1),
                XContentType.JSON));
        bulkRequest.add(client.prepareIndex(indexName, type, e2.getId()).setSource(mapper.writeValueAsString(e2),
                XContentType.JSON));
        bulkRequest.add(client.prepareIndex(indexName, type, e3.getId()).setSource(mapper.writeValueAsString(e3),
                XContentType.JSON));

        BulkResponse bulkResponse = bulkRequest.get();
        if (display) {
            bulkResponse.forEach(k -> {
                System.out.println(k.getId());
            });
        }
        assertFalse(bulkResponse.hasFailures());
        assertTrue(bulkRequest.numberOfActions() == 3);

        // when
        BulkByScrollResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(QueryBuilders.termQuery("name", "entity1")) // query
                .source(indexName) // index
                .get();

        //System.out.println(response.getDeleted());
        // assertTrue(response.getDeleted() == 2L);
    }

    // again
    @Test
    public void update() throws IOException, InterruptedException, ExecutionException{
        // given
        SampleEntity e1 = new SampleEntity("1", "entity1", 10, new Date());
        SampleEntity e2 = new SampleEntity("2", "entity2", 10, new Date());
        SampleEntity e3 = new SampleEntity("3", "entity3", 20, new Date());

        BulkRequestBuilder bulkRequest = client.prepareBulk();
        bulkRequest.add(client.prepareIndex(indexName, type, e1.getId()).setSource(mapper.writeValueAsString(e1),
                XContentType.JSON));
        bulkRequest.add(client.prepareIndex(indexName, type, e2.getId()).setSource(mapper.writeValueAsString(e2),
                XContentType.JSON));
        bulkRequest.add(client.prepareIndex(indexName, type, e3.getId()).setSource(mapper.writeValueAsString(e3),
                XContentType.JSON));

        BulkResponse bulkResponse = bulkRequest.get();
        assertFalse(bulkResponse.hasFailures());

        Gson gson = new Gson();

        e1.setName("modified entity");

        final XContentBuilder contentBuilder = XContentFactory.jsonBuilder();
        try {
            contentBuilder.startObject();
            contentBuilder.field("name", e1.getName());
            contentBuilder.endObject();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        // // when
        // UpdateRequest updateRequest = new UpdateRequest();
        // updateRequest.index(indexName);
        // updateRequest.type(type);
        // updateRequest.id(e1.getId());
        // updateRequest.doc(mapper.writeValueAsBytes(e1), SampleEntity.class);
        // UpdateResponse updateResponse = client.update(updateRequest).get();
        // System.out.println(updateResponse.getResult().name());

        final BulkRequestBuilder bulkRequest2 = client.prepareBulk();
        bulkRequest2.add(client.prepareUpdate(indexName, type, e1.getId()).setDetectNoop(false).setDoc(contentBuilder));
        BulkResponse updateResponse = bulkRequest2.execute().actionGet();
        assertFalse(updateResponse.hasFailures());

        GetResponse getResponse = client.prepareGet(indexName, type, e1.getId()).setOperationThreaded(false).get();
        SampleEntity readInst = mapper.readValue(getResponse.getSourceAsBytes(), SampleEntity.class);
        assertThat(e1.getId(), is(readInst.getId()));
        assertThat(e1.getName(), is(readInst.getName()));
    }

    @Test
    public void multiGet(){
        // given
        SampleEntity e1 = new SampleEntity("1", "entity1", 10, new Date());
        SampleEntity e2 = new SampleEntity("2", "entity2", 10, new Date());
        SampleEntity e3 = new SampleEntity("3", "entity3", 20, new Date());
        try {
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            bulkRequest.add(client.prepareIndex(indexName, type, e1.getId()).setSource(mapper.writeValueAsString(e1),
                    XContentType.JSON));
            bulkRequest.add(client.prepareIndex(indexName, type, e2.getId()).setSource(mapper.writeValueAsString(e2),
                    XContentType.JSON));
            bulkRequest.add(client.prepareIndex(indexName, type, e3.getId()).setSource(mapper.writeValueAsString(e3),
                    XContentType.JSON));

            BulkResponse bulkResponse = bulkRequest.get();
            assertFalse(bulkResponse.hasFailures());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        MultiGetResponse multiGetItemResponse = client.prepareMultiGet().add(indexName, type, e1.getId()) // get by a single id
                .add(indexName, type, e2.getId(), e3.getId()) // get by a list of id
                .get();
        multiGetItemResponse.forEach(r -> {
            GetResponse response = r.getResponse();
            if (display && response.isExists()) {
                System.out.println(response.getSourceAsString());
            }
        });
    }

    @Test
    public void bulkProcessor() throws IOException {
        BulkProcessor bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request){
                System.out.println("## [before bulk] : " + DateUtil.getDateString("hh:mm:ss.SSS", new Date()));
                
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response){
                System.out.println("## [after bulk] : " + DateUtil.getDateString("hh:mm:ss.SSS", new Date()));
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure){
                System.out.println("## [after bulk with Throwable] : " + DateUtil.getDateString("hh:mm:ss.SSS", new Date()));
            }
        }).setBulkActions(1000).setConcurrentRequests(0).build();
        
        for(int i=0; i<1000; i++) {            
            SampleEntity e = new SampleEntity(""+i, "entity"+i, i, new Date());
            bulkProcessor.add(new IndexRequest(indexName,type,""+i).source(mapper.writeValueAsString(e),
                    XContentType.JSON));                        
        }
        
        bulkProcessor.flush();
        // or
        //bulkProcessor.close();
        
        // Refresh indices
        client.admin().indices().prepareRefresh(indexName).get();
        
        SearchResponse searchResponse = client.prepareSearch(indexName).get();
        assertTrue(searchResponse.getHits().getTotalHits() == 1000L);        
    }
}
