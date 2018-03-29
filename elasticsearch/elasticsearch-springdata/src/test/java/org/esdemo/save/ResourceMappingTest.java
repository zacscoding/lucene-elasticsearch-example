package org.esdemo.save;

import org.elasticsearch.index.query.TermQueryBuilder;
import org.esdemo.AbstractTestRunner;
import org.esdemo.entity.ResourceMappingTestEntity;
import org.esdemo.repository.ResourceMappingTestRepository;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

public class ResourceMappingTest extends AbstractTestRunner {
    // @Autowired
    protected ResourceMappingTestRepository resourceMappingTestRepository;

    @Test
    public void saveAndSearch() {
        elasticsearchTemplate.deleteIndex(ResourceMappingTestEntity.class);
        ResourceMappingTestEntity e = new ResourceMappingTestEntity(null, "name", "salary in company", "text");
        resourceMappingTestRepository.save(e);

        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(new TermQueryBuilder("salary-in-company", "salary"))
                .build();
        Page<ResourceMappingTestEntity> results = resourceMappingTestRepository.search(query);
        System.out.println(results.getTotalElements());
        //assertTrue(.getTotalElements() == 1L);
    }
}
