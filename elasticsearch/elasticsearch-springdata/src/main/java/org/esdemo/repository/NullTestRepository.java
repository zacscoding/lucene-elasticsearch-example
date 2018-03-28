package org.esdemo.repository;

import org.esdemo.entity.NullTestEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface NullTestRepository extends ElasticsearchRepository<NullTestEntity, String> {
}
