package org.esdemo.search;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.esdemo.AbstractTestRunner;
import org.esdemo.entity.NullTestEntity;
import org.esdemo.repository.NullTestRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class NullTest extends AbstractTestRunner {

    @Autowired
    NullTestRepository nullTestRepository;

    @Before
    public void setUp() {
        nullTestRepository.deleteAll();
        nullTestRepository.saveAll(Arrays.asList(
                // id, name, hobby, age
                new NullTestEntity(null, "name1", "hobby1", 1),
                new NullTestEntity(null, "name2", null, null),
                new NullTestEntity(null, null, null, 1)
        ));
    }

    @Test
    public void nullSearch() {
        // tag :: search - name is null or not null
        // search null
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(new BoolQueryBuilder().mustNot(new ExistsQueryBuilder("name")))
                .build();
        Page<NullTestEntity> results = nullTestRepository.search(searchQuery);
        assertTrue(results.getTotalElements() == 1L);
        results.forEach(e -> assertTrue(e.getName() == null));

        // search not null
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(new BoolQueryBuilder().must(new ExistsQueryBuilder("name")))
                .build();
        results = nullTestRepository.search(searchQuery);
        assertTrue(results.getTotalElements() == 2L);
        results.forEach(e -> assertTrue(e.getName() != null));
        // -- tag :: end

        // tag :: search - age is null or not null
        // age null
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(new BoolQueryBuilder().mustNot(new ExistsQueryBuilder("age")))
                .build();
        results = nullTestRepository.search(searchQuery);
        assertTrue(results.getTotalElements() == 1L);
        results.forEach(e -> assertTrue(e.getAge() == null));

        // age not null
        searchQuery = new NativeSearchQueryBuilder()
                .withQuery(new BoolQueryBuilder().must(new ExistsQueryBuilder("age")))
                .build();
        results = nullTestRepository.search(searchQuery);
        assertTrue(results.getTotalElements() == 2L);
        results.forEach(e -> assertTrue(e.getAge() != null));
    }
}
