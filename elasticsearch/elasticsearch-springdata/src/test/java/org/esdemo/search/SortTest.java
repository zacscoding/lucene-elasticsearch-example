package org.esdemo.search;

import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.esdemo.AbstractTestRunner;
import org.esdemo.entity.SortTestEntity;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.ArrayList;
import java.util.List;

public class SortTest extends AbstractTestRunner {

    List<SortTestEntity> entities;

    @Before
    public void setUp() {
        sortTestRepository.deleteAll();

        entities = new ArrayList<>();
        entities.add(new SortTestEntity(null, "test1", 2, 1));
        entities.add(new SortTestEntity(null, "test2", 2, 2));
        entities.add(new SortTestEntity(null, "test3", 2, 3));
        entities.add(new SortTestEntity(null, "test4", 1, 1));
        entities.add(new SortTestEntity(null, "test5", 1, 2));
        entities.add(new SortTestEntity(null, "test6", 1, 3));

        sortTestRepository.saveAll(entities);
    }

    @Test
    public void sort() {
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        SearchQuery searchQuery = builder.withPageable(PageRequest.of(0, 10))
                .withSort(new FieldSortBuilder("age").order(SortOrder.DESC))
                .withSort(new FieldSortBuilder("salary").order(SortOrder.ASC))
                .build();

        // Result :: "test1", "test2", "test3", "test4", "test5", "test6"
        Page<SortTestEntity> results = sortTestRepository.search(searchQuery);
        results.getContent().forEach(e -> System.out.println(e));
    }

}
