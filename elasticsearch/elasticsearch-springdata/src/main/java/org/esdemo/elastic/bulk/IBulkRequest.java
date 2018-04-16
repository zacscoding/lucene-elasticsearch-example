package org.esdemo.elastic.bulk;

/**
 * @author zacconding
 * @Date 2018-04-11
 * @GitHub : https://github.com/zacscoding
 */
public interface IBulkRequest {
    Object getRequestInstance();

    BulkItemResponseConsumer getConsumer();
}