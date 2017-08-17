package com.elasticsearch_java.api;

import org.elasticsearch.client.transport.TransportClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.elasticsearch_java.app.ClientFactory;

public class ElasticTestCase {
	static TransportClient client;
	
	@BeforeClass 
    public static void setupOnce() {
        client = ClientFactory.getClient();
    }
    @AfterClass
    public static void teardownOnce() {
        if(client != null)
            client.close();
    }
	

}
