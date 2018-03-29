package org.esdemo.repository;

import org.esdemo.entity.MappingTestEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MappingTestRepository extends ElasticsearchRepository<MappingTestEntity, String> {

}
