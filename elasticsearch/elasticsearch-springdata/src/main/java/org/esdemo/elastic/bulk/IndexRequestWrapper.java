package org.esdemo.elastic.bulk;

import org.elasticsearch.action.index.IndexRequest;

/**
 * @author zacconding
 * @Date 2018-04-11
 * @GitHub : https://github.com/zacscoding
 */
public class IndexRequestWrapper extends IndexRequest implements IBulkRequest {

    private BulkItemResponseConsumer responseConsumer;
    private Object requestInstance;

    public IndexRequestWrapper() {
        super();
    }

    public IndexRequestWrapper(String index) {
        super(index);
    }

    public IndexRequestWrapper(String index, String type) {
        super(index, type);
    }

    public IndexRequestWrapper(String index, String type, String id) {
        super(index, type, id);
    }

    public void setResponseConsumer(BulkItemResponseConsumer responseConsumer) {
        this.responseConsumer = responseConsumer;
    }

    public void setRequestInstance(Object requestInstance) {
        this.requestInstance = requestInstance;
    }

    @Override
    public Object getRequestInstance() {
        return requestInstance;
    }

    @Override
    public BulkItemResponseConsumer getConsumer() {
        return responseConsumer;
    }
}
