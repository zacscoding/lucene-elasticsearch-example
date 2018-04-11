package org.esdemo.elastic.bulk;

import org.elasticsearch.action.bulk.BulkItemResponse;

/**
 *
 * @author zacconding
 * @Date 2018-04-11
 * @GitHub : https://github.com/zacscoding
 */

@FunctionalInterface
public interface BulkItemResponseConsumer {
    void accept(BulkItemResponse res, Object inst);
}