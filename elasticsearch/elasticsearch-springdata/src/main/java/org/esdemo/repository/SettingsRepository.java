package org.esdemo.repository;

import org.esdemo.entity.SettingsEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author zacconding
 * @Date 2018-04-05
 * @GitHub : https://github.com/zacscoding
 */
public interface SettingsRepository extends ElasticsearchRepository<SettingsEntity, String> {
}
