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
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
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
    public void setUp() {
        indexName = "sample";
        type = "entity";
        display = false;
        mapper = new ObjectMapper();
        try {
            DeleteIndexResponse deleteResponse  = client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
        }
        catch(Exception e) {
            // ignore
        }
        CreateIndexResponse createResponse = client.admin().indices().create(new CreateIndexRequest(indexName)).actionGet();
    }
    
    @BeforeClass 
    public static void testSetUp() {
        client = ClientFactory.getClient();
    }
    @AfterClass
    public static void testTearDown() {
        if(client != null)
            client.close();
    }    
    
   // @Test
    public void saveAndGet() throws Exception {
        SampleEntity entity = new SampleEntity();
        entity.setId("1");
        entity.setName("entity1");
        entity.setRegDate(new Date());
        entity.setAge(10);
        
        // save
        // IndexResponse response = client.prepareIndex("sample","entity",entity.getId()).setSource(mapper.writeValueAsString(entity)).get();
        IndexResponse response = client.prepareIndex(indexName,type,entity.getId()).setSource(mapper.writeValueAsString(entity),XContentType.JSON).get();
                
        // result
        String _index = response.getIndex();
        String _type = response.getType();
        String _id = response.getId();
        long _version = response.getVersion();
        RestStatus status = response.status();
                
        if(display) {
            System.out.println("index : " + _index);
            System.out.println("_type : " + _type);
            System.out.println("_id : " + _id);
            System.out.println("_version : " + _version);
            System.out.println("status_ : " + status.getStatus());            
        }
        
        GetResponse getResponse = client.prepareGet(indexName, type, entity.getId()).setOperationThreaded(false).get();
        if(display) {
            getResponse.getSource().forEach((k,v)->{System.out.println("key : " + k + ", value : " + v);});
        }
    }
    
   // @Test
    public void delete() throws JsonProcessingException {
        SampleEntity entity = new SampleEntity();
        entity.setId("1");
        entity.setName("entity1");
        entity.setRegDate(new Date());
        entity.setAge(10);
        
        ObjectMapper mapper = new ObjectMapper();
                
        // save
        // IndexResponse response = client.prepareIndex("sample","entity",entity.getId()).setSource(mapper.writeValueAsString(entity)).get();
        IndexResponse response = client.prepareIndex(indexName, type,entity.getId()).setSource(mapper.writeValueAsString(entity),XContentType.JSON).get();
        assertTrue(response.status().getStatus()==201);
        
        // delete
        DeleteResponse deleteResponse = client.prepareDelete(indexName, type,entity.getId()).get();
        assertThat(deleteResponse.status().name(),is("OK"));
        GetResponse getResponse = client.prepareGet(indexName,  type, entity.getId()).setOperationThreaded(false).get();
        assertNull(getResponse.getSource());                
    }
    
   // @Test
    public void deleteByQuery() throws JsonProcessingException {
        //https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-docs-delete-by-query.html
        // given
        SampleEntity e1 = new SampleEntity("1", "entity1", 10, new Date());
        SampleEntity e2 = new SampleEntity("2", "entity2", 10, new Date());
        SampleEntity e3 = new SampleEntity("3", "entity3", 20, new Date());
        
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        bulkRequest.add(client.prepareIndex(indexName, type,e1.getId()).setSource(mapper.writeValueAsString(e1),XContentType.JSON));
        bulkRequest.add(client.prepareIndex(indexName, type,e2.getId()).setSource(mapper.writeValueAsString(e2),XContentType.JSON));
        bulkRequest.add(client.prepareIndex(indexName, type,e3.getId()).setSource(mapper.writeValueAsString(e3),XContentType.JSON));
        
        BulkResponse bulkResponse = bulkRequest.get();
        if(display) {
            bulkResponse.forEach(k-> {
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
        
        System.out.println(response.getDeleted());
        //assertTrue(response.getDeleted() == 2L);        
    }
    
    // again
    @Test
    public void update() throws IOException, InterruptedException, ExecutionException {
        // given
        SampleEntity e1 = new SampleEntity("1", "entity1", 10, new Date());
        SampleEntity e2 = new SampleEntity("2", "entity2", 10, new Date());
        SampleEntity e3 = new SampleEntity("3", "entity3", 20, new Date());
        
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        bulkRequest.add(client.prepareIndex(indexName, type,e1.getId()).setSource(mapper.writeValueAsString(e1),XContentType.JSON));
        bulkRequest.add(client.prepareIndex(indexName, type,e2.getId()).setSource(mapper.writeValueAsString(e2),XContentType.JSON));
        bulkRequest.add(client.prepareIndex(indexName, type,e3.getId()).setSource(mapper.writeValueAsString(e3),XContentType.JSON));
        
        BulkResponse bulkResponse = bulkRequest.get();
        assertFalse(bulkResponse.hasFailures());
        
        Gson gson = new Gson();
        SampleEntity mod1 = new SampleEntity();
        mod1.setId(e1.getId());
        mod1.setName("modify1");
        
        e1.setName("modify1");
        
        // when
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(indexName);
        updateRequest.type(type);
        updateRequest.id(mod1.getId());
        updateRequest.doc(mapper.writeValueAsBytes(e1), SampleEntity.class);
        UpdateResponse updateResponse = client.update(updateRequest).get();
        System.out.println(updateResponse.getResult().name());
        
        GetResponse getResponse = client.prepareGet(indexName, type, mod1.getId()).setOperationThreaded(false).get();        
        SampleEntity readInst = mapper.readValue(getResponse.getSourceAsBytes(), SampleEntity.class);
        assertThat(mod1.getId(), is(readInst.getId()));
        assertThat(mod1.getName(), is(readInst.getName()));
        
    }
    
    
    
    
    
    
    
    
    
}
