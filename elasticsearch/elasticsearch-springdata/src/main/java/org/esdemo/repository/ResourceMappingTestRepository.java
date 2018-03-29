package org.esdemo.repository;

import org.esdemo.entity.ResourceMappingTestEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ResourceMappingTestRepository extends ElasticsearchRepository<ResourceMappingTestEntity, String> {
}
