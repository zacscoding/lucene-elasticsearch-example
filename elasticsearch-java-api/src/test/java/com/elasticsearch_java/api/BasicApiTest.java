package com.elasticsearch_java.api;

import java.util.Date;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.elasticsearch_java.entity.SampleEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BasicApiTest extends ElasticTestCase {	
	@Before
    public void setUp() {
			
    }
    
    @Test
    @Ignore
    public void saveAndGet() throws Exception {
        SampleEntity entity = new SampleEntity();
        entity.setId("1");
        entity.setName("new entity1");
        entity.setRegDate(new Date());
        entity.setAge(10);
        
        ObjectMapper mapper = new ObjectMapper();
        byte[] json = mapper.writeValueAsBytes(entity);
                
        // save
        IndexResponse response = client.prepareIndex("sample","entity",entity.getId())
                                        .setSource(json).get();
        
        // result
        String _index = response.getIndex();
        String _type = response.getType();
        String _id = response.getId();
        long _version = response.getVersion();
        boolean created = response.isCreated();        
                
        System.out.println("index : " + _index);
        System.out.println("_type : " + _type);
        System.out.println("_id : " + _id);
        System.out.println("_version : " + _version);
        System.out.println("created : " + created);
        
        // Get API
        GetResponse getResponse = client.prepareGet("sample", "entity", entity.getId()).setOperationThreaded(false).get();
        
        // get class from GetResponse
        SampleEntity readEntity = mapper.readValue(getResponse.getSourceAsString(), SampleEntity.class);
        // get Map from GetResponse
        //getResponse.getSource().forEach((k,v)->{System.out.println("key : " + k + ", value : " + v);});        
    }
    
    @Test
    public void saveAndDelete() {
    	System.out.println("k");
    }
    
    
    
    
}
