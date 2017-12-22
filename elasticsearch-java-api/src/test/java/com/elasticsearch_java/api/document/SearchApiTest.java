package com.elasticsearch_java.api.document;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.elasticsearch_java.app.ClientFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SearchApiTest {
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
    
    

}
