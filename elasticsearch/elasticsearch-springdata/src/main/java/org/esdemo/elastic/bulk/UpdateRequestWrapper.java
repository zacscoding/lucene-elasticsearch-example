package org.esdemo.elastic.bulk;

import org.elasticsearch.action.update.UpdateRequest;

/**
 * @author zacconding
 * @Date 2018-04-11
 * @GitHub : https://github.com/zacscoding
 */
public class UpdateRequestWrapper extends UpdateRequest implements IBulkRequest {

    private BulkItemResponseConsumer successConsumer;
    private BulkItemResponseConsumer failConsumer;
    private Object requestInstance;


    public UpdateRequestWrapper() {
        super();
    }

    public UpdateRequestWrapper(String index, String type, String id) {
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
