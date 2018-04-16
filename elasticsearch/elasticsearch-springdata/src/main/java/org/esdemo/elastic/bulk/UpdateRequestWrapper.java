package org.esdemo.elastic.bulk;

import org.elasticsearch.action.update.UpdateRequest;

/**
 * @author zacconding
 * @Date 2018-04-11
 * @GitHub : https://github.com/zacscoding
 */
public class UpdateRequestWrapper extends UpdateRequest implements IBulkRequest {

    private BulkItemResponseConsumer responseConsumer;
    private Object requestInstance;


    public UpdateRequestWrapper() {
        super();
    }

    public UpdateRequestWrapper(String index, String type, String id) {
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
