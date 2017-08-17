package com.elasticsearch_java.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.elasticsearch_java.entity.SampleEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Java API 
 * https://www.elastic.co/guide/en/elasticsearch/client/java-api/2.4
 * 
 * @author zaccoding
 * @Date 2017. 8. 17.
 */
public class BasicApiTest extends ElasticTestCase {	
	private ObjectMapper objectMapper;
	@Before
    public void setUp() throws Exception {
		objectMapper = new ObjectMapper();
		IndicesAdminClient adminClient = client.admin().indices();
		
		ActionFuture<IndicesExistsResponse> future = adminClient.exists(new IndicesExistsRequest("sample"));
		
		if(future.actionGet().isExists()) {
			adminClient.delete(new DeleteIndexRequest("sample")).actionGet();			
		}		
    }
    
    @Test
    @Ignore
    public void saveAndGet() throws Exception {
        SampleEntity entity = new SampleEntity();
        entity.setId("1");
        entity.setName("new entity1");
        entity.setRegDate(new Date());
        entity.setAge(10);
                
        byte[] json = objectMapper.writeValueAsBytes(entity);
                
        // save
        IndexResponse response = client.prepareIndex("sample","entity",entity.getId())
                                        .setSource(json).get();
        
        // result
        /*
		String _index = response.getIndex();
        String _type = response.getType();
        String _id = response.getId();
        long _version = response.getVersion();
        boolean created = response.isCreated();        
                
        System.out.println("index : " + _index);
        System.out.println("_type : " + _type);
        System.out.println("_id : " + _id);
        System.out.println("_version : " + _version);
        System.out.println("created : " + created);*/
        
        // Get API
        GetResponse getResponse = client.prepareGet("sample", "entity", entity.getId()).setOperationThreaded(false).get();
        
        // get class from GetResponse
        SampleEntity readEntity = objectMapper.readValue(getResponse.getSourceAsString(), SampleEntity.class);
        
        assertTrue(isSampeEntity(entity,readEntity));
        // get Map from GetResponse
        //getResponse.getSource().forEach((k,v)->{System.out.println("key : " + k + ", value : " + v);});        
    }
    
    @Test
    @Ignore
    public void saveAndDelete() throws Exception {
    	SampleEntity entity = new SampleEntity();
        entity.setId("1");
        entity.setName("entity1");
        entity.setRegDate(new Date());
        entity.setAge(10);
        
        byte[] json = objectMapper.writeValueAsBytes(entity);
                
        // save
        IndexRequestBuilder indexRequestBuiilder = client.prepareIndex("sample","entity",entity.getId()).setSource(json);
        IndexResponse indexResponse = indexRequestBuiilder.get();
        
        assertThat(entity.getId(),is(indexResponse.getId()));
        
        // delete
        DeleteRequestBuilder deleteRequestBuilder = client.prepareDelete("sample", "entity", entity.getId());
        DeleteResponse deleteResponse = deleteRequestBuilder.get();
        assertTrue(deleteResponse.isFound());
        
        // find 
        GetResponse getResponse = client.prepareGet("sample", "entity", entity.getId()).setOperationThreaded(false).get();
        assertFalse(getResponse.isExists());        
    }
    
    @Test
    @Ignore
    public void update() throws Exception {    	
    	SampleEntity entity = new SampleEntity();
        entity.setId("1");
        entity.setName("entity1");
        entity.setRegDate(new Date());
        entity.setAge(10);
        
        byte[] json = objectMapper.writeValueAsBytes(entity);
                
        // save
        IndexRequestBuilder indexRequestBuiilder = client.prepareIndex("sample","entity",entity.getId()).setSource(json);
        IndexResponse indexResponse = indexRequestBuiilder.get();
        assertTrue(indexResponse.isCreated());
        
        // update
        entity.setName("update entity1");
        byte[] updateJson = objectMapper.writeValueAsBytes(entity);
        
        UpdateResponse updateResponse = client.prepareUpdate("sample", "entity", entity.getId()).setDoc(updateJson).get();
        System.out.println(updateResponse.isCreated());
        
        // Get API
        GetResponse getResponse = client.prepareGet("sample", "entity", entity.getId()).setOperationThreaded(false).get();
        SampleEntity readEntity = objectMapper.readValue(getResponse.getSourceAsString(), SampleEntity.class);
        assertTrue(isSampeEntity(entity,readEntity));
        
    }
    
    @Test
    public void upsert() throws Exception {
    	SampleEntity entity = new SampleEntity();
        entity.setId("1");
        entity.setName("entity1");
        entity.setRegDate(new Date());
        entity.setAge(10);        
        byte[] json = objectMapper.writeValueAsBytes(entity);
        
    	// index request
        IndexRequest indexRequest = new IndexRequest("sample","entity","1").source(json);        
        		
    	// upsert(insert) if the document does not exist
    	Map<String,Object> map = new HashMap<>();
    	map.put("name","upsert entity1");    	
    	UpdateRequest updateRequest = new UpdateRequest("sample","entity","1").doc(map).upsert(indexRequest);    	
    	UpdateResponse updateResponse = client.update(updateRequest).get();    	
    	GetResponse getResponse = client.prepareGet("sample", "entity", entity.getId()).setOperationThreaded(false).get();
    	SampleEntity readEntity = objectMapper.readValue(getResponse.getSourceAsString(), SampleEntity.class);
    	assertTrue(isSampeEntity(entity, readEntity));
    	
    	// upsert(update) if the document does exist
    	client.update(updateRequest).get();
    	GetResponse getResponse2 = client.prepareGet("sample", "entity", entity.getId()).setOperationThreaded(false).get();
    	SampleEntity readEntity2 = objectMapper.readValue(getResponse2.getSourceAsString(), SampleEntity.class);
    	assertThat(entity.getId(),is(readEntity2.getId()));
    	assertThat(entity.getRegDate(),is(readEntity2.getRegDate()));
    	assertThat(entity.getAge(),is(readEntity2.getAge()));
    	assertThat(readEntity2.getName(),is("upsert entity1"));
    }
        
    
    private boolean isSampeEntity(SampleEntity e1, SampleEntity e2) {
    	if(e1 == null && e2 == null)
    		return true;
    	if(e1 == null || e2 == null)
    		return false;
    	
    	if(!e1.getId().equals(e2.getId())) {
    		return false;
    	}
    	
    	if(!e1.getAge().equals(e2.getAge())) {
    		return false;
    	}
    	
    	if(!e1.getName().equals(e2.getName()))
    		return false;
    	
    	if(!e1.getRegDate().equals(e2.getRegDate()))
    		return false;
    	
    	return true;
    }
    
    
    
}
