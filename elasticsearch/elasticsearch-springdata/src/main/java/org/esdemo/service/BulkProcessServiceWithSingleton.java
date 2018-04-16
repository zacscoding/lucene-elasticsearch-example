package org.esdemo.service;

import javax.annotation.PostConstruct;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.esdemo.elastic.bulk.BulkItemResponseConsumer;
import org.esdemo.elastic.bulk.IBulkRequest;
import org.esdemo.util.SimpleLogger;
import org.esdemo.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Having singleton bulk processor
 *
 * @author zacconding
 * @Date 2018-04-11
 * @GitHub : https://github.com/zacscoding
 */
@Service
public class BulkProcessServiceWithSingleton {

    private static final Logger logger = LoggerFactory.getLogger(BulkProcessServiceWithSingleton.class);

    @Autowired
    private Client client;

    // can be spring bean
    private BulkProcessor bulkProcessor;

    @PostConstruct
    public void setUp() {
        initialize();
    }

//    public void add(Object request) {
//        if(request instanceof DocWriteRequest) {
//            bulkProcessor.add((DocWriteRequest) request);
//        } else {
//            logger.error("Failed to cast DocWriteRequest. request class : " + request.getClass().getName());
//        }
//    }

    public void add(IndexRequest indexRequest) {
        bulkProcessor.add(indexRequest);
    }

    public void flush() {
        bulkProcessor.flush();
    }

    private void initialize() {
        bulkProcessor = createBulkProcessor();
    }

    private BulkProcessor createBulkProcessor() {
        return BulkProcessor.builder(client, new Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                SimpleLogger.printTitle(ThreadUtil.getCurrentThreadInform() + " : Before bulk : " + executionId);
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                SimpleLogger.printTitle(ThreadUtil.getCurrentThreadInform() + " : After bulk : " + executionId);
                // handle after bulk
                for (BulkItemResponse itemResponse : response.getItems()) {
                    DocWriteRequest req = request.requests().get(itemResponse.getItemId());
                    if (!(req instanceof IBulkRequest)) {
                        SimpleLogger.println("## Cant cast IBUlkRequest");
                        continue;
                    }

                    IBulkRequest bulkRequest = (IBulkRequest) req;
                    BulkItemResponseConsumer consumer = bulkRequest.getConsumer();
                    if (consumer != null && bulkRequest.getRequestInstance() != null) {
                        SimpleLogger.println("## Accept response");
                        consumer.accept(itemResponse, bulkRequest.getRequestInstance());
                    }
                }
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                SimpleLogger.printTitle(ThreadUtil.getCurrentThreadInform() + " : After bulk : " + executionId + "with exception");
                logger.error("## failed to bulk and receipt exception", failure);
            }
        }).setBulkActions(3).setConcurrentRequests(1)
                            // .setBulkSize(new ByteSizeValue(10, ByteSizeUnit.MB))
                            .setFlushInterval(TimeValue.timeValueSeconds(3L))
                            //.setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                            .build();
    }
}