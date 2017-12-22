//package com.elasticsearch_java.app;
//
//import java.net.InetAddress;
//import java.util.Date;
//
//import org.apache.log4j.Logger;
//import org.elasticsearch.action.get.GetResponse;
//import org.elasticsearch.action.index.IndexResponse;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.transport.InetSocketTransportAddress;
//import org.elasticsearch.transport.client.PreBuiltTransportClient;
//
//import com.elasticsearch_java.entity.SampleEntity;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//public class App {    
//    private static final Logger logger = Logger.getLogger(App.class);    
//    public static void main(String[] args) throws Exception {
//        Settings settings = Settings.builder()
//                .put("cluster.name", "es_test").build();
//        
//        TransportClient client = new PreBuiltTransportClient(settings)
//                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
//
//        SampleEntity entity = new SampleEntity();
//        entity.setId("1");
//        entity.setName("entity1");
//        entity.setRegDate(new Date());
//        entity.setAge(10);
//        
//        ObjectMapper mapper = new ObjectMapper();
//        byte[] json = mapper.writeValueAsBytes(entity);
//        
//        
//        // save
//        IndexResponse response = client.prepareIndex("sample","entity",entity.getId())
//                                        .setSource(json).get();
//                
//        // result
//        String _index = response.getIndex();
//        String _type = response.getType();
//        String _id = response.getId();
//        long _version = response.getVersion();
//                
//        System.out.println("index : " + _index);
//        System.out.println("_type : " + _type);
//        System.out.println("_id : " + _id);
//        System.out.println("_version : " + _version);
//        
//        GetResponse getResponse = client.prepareGet("sample", "entity", entity.getId()).setOperationThreaded(false).get();
//        getResponse.getSource().forEach((k,v)->{System.out.println("key : " + k + ", value : " + v);});
//        
//        // on shutdown
//        client.close(); 
//    }
//}
//
