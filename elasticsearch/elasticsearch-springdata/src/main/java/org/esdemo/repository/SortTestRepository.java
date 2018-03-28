package org.esdemo.repository;

import org.esdemo.entity.SortTestEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SortTestRepository extends ElasticsearchRepository<SortTestEntity, String> {

}
