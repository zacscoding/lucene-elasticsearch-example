package org.esdemo.repository;

import org.esdemo.entity.ReceiptEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author zacconding
 * @Date 2018-04-03
 * @GitHub : https://github.com/zacscoding
 */
public interface ReceiptRepository extends ElasticsearchRepository<ReceiptEntity, String> {

}
