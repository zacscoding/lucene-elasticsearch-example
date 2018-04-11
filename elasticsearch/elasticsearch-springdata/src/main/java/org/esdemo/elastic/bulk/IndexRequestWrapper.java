package org.esdemo.elastic.bulk;

import org.elasticsearch.action.index.IndexRequest;

/**
 * @author zacconding
 * @Date 2018-04-11
 * @GitHub : https://github.com/zacscoding
 */
public class IndexRequestWrapper extends IndexRequest implements IBulkRequest {

    private BulkItemResponseConsumer successConsumer;
    private BulkItemResponseConsumer failConsumer;
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

    public void setSuccessConsumer(BulkItemResponseConsumer successConsumer) {
        this.successConsumer = successConsumer;
    }

    public void setFailConsumer(BulkItemResponseConsumer failConsumer) {
        this.failConsumer = failConsumer;
    }

    public void setRequestInstance(Object requestInstance) {
        this.requestInstance = requestInstance;
    }

    @Override
    public Object getRequestInstance() {
        return requestInstance;
    }

    @Override
    public BulkItemResponseConsumer getConsumer(boolean isFailed) {
        return isFailed ? failConsumer : successConsumer;
    }
}
