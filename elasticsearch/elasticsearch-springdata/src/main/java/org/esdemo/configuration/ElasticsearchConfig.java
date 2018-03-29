//package org.esdemo.configuration;
//
//import org.elasticsearch.client.Client;
//import org.esdemo.util.SimpleLogger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
//import org.springframework.data.elasticsearch.core.EntityMapper;
//
//@Configuration
//public class ElasticsearchConfig {
//
//    @Autowired
//    Client client;
//
//    @Autowired
//    EntityMapper entityMapper;
//
//    @Bean
//    public ElasticsearchTemplate elasticsearchTemplate() {
//        SimpleLogger.println("elasticsearch template created {} , {}", client, entityMapper);
//        return new ElasticsearchTemplate(client, entityMapper);
//    }
//}
