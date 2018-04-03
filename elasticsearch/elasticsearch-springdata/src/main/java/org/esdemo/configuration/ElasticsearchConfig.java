//package org.esdemo.configuration;
//
//import org.elasticsearch.action.bulk.BulkProcessor;
//import org.elasticsearch.action.bulk.BulkProcessor.Listener;
//import org.elasticsearch.action.bulk.BulkRequest;
//import org.elasticsearch.action.bulk.BulkResponse;
//import org.elasticsearch.client.Client;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class ElasticsearchConfig {
//
//    @Value("${elasticsearch.bulk.actions}")
//    private int bulkActions;
//    @Value("${elasticsearch.bulk.conccurent.request}")
//    private int concurrentRequest;
//
//    @Autowired
//    private Client client;
//
//    @Bean
//    public BulkProcessor bulkProcessor() {
//        return BulkProcessor.builder(client, getBulkListener())
//            .setBulkActions(bulkActions).setConcurrentRequests(concurrentRequest).build();
//    }
//
//    private BulkProcessor.Listener getBulkListener() {
//        return new Listener() {
//            @Override
//            public void beforeBulk(long l, BulkRequest bulkRequest) {
//            }
//
//            @Override
//            public void afterBulk(long l, BulkRequest bulkRequest, BulkResponse bulkResponse) {
//
//            }
//
//            @Override
//            public void afterBulk(long l, BulkRequest bulkRequest, Throwable throwable) {
//
//            }
//        };
//    }
//
//
////    @Autowired
////    Client client;
////
////    @Autowired
////    EntityMapper entityMapper;
////
////    @Bean
////    public ElasticsearchTemplate elasticsearchTemplate() {
////        SimpleLogger.println("elasticsearch template created {} , {}", client, entityMapper);
////        return new ElasticsearchTemplate(client, entityMapper);
////    }
//}
