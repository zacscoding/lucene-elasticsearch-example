package com.elasticsearch_java.api;

import java.util.Date;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.rest.RestStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.elasticsearch_java.app.ClientFactory;
import com.elasticsearch_java.entity.SampleEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BasicApiTest {
    private static TransportClient client;
    
    @BeforeClass 
    public static void staticSetUp() {
        client = ClientFactory.getClient();
    }
    @AfterClass
    public static void staticEndUp() {
        if(client != null)
            client.close();
    }
    
    @Test
    public void saveAndGet() throws Exception {
        SampleEntity entity = new SampleEntity();
        entity.setId("1");
        entity.setName("entity1");
        entity.setRegDate(new Date());
        entity.setAge(10);
        
        ObjectMapper mapper = new ObjectMapper();
        byte[] json = mapper.writeValueAsBytes(entity);
                
        // save
        IndexResponse response = client.prepareIndex("sample","entity",entity.getId()).setSource(mapper.writeValueAsString(entity)).get();                                        
                
        // result
        String _index = response.getIndex();
        String _type = response.getType();
        String _id = response.getId();
        long _version = response.getVersion();
        RestStatus status = response.status();
                
        System.out.println("index : " + _index);
        System.out.println("_type : " + _type);
        System.out.println("_id : " + _id);
        System.out.println("_version : " + _version);
        System.out.println("status_ : " + status.getStatus());
        
        GetResponse getResponse = client.prepareGet("sample", "entity", entity.getId()).setOperationThreaded(false).get();
        getResponse.getSource().forEach((k,v)->{System.out.println("key : " + k + ", value : " + v);});        
    }
}
