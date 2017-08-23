package com.elasticsearch_java.app;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class ClientFactory {
    private static final String CLUSTER_NAME = "es_test";
    private static final String HOST_IP = "192.168.100.149";
    private static final int TRANSPORT = 9300; 
    //private static final String HOST_IP = "127.0.0.1";
    //private static final int TRANSPORT = 9200;
    private static TransportClient CLIENT;
    
    public static TransportClient getClient() {
        if(CLIENT == null)
            initClient();
        return CLIENT;        
    }
    
    private static void initClient() {
        Settings settings = Settings.builder()
                .put("cluster.name", CLUSTER_NAME).build();        
        try {
            CLIENT = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(HOST_IP),TRANSPORT ));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("ClientFactory::finalize()");
        if(CLIENT != null)
            CLIENT.close();
    }
    

}
