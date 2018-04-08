package org.esdemo.save.mapping;

import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.esdemo.AbstractTestRunner;
import org.esdemo.entity.MappingTestEntity;
import org.esdemo.repository.MappingTestRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class MappingKeywordTest extends AbstractTestRunner {

    @Autowired
    protected MappingTestRepository mappingTestRepository;

    @Test
    public void keywordTest() {
        elasticsearchTemplate.deleteIndex(MappingTestEntity.class);
        elasticsearchTemplate.createIndex(MappingTestEntity.class);
        elasticsearchTemplate.putMapping(MappingTestEntity.class);
        elasticsearchTemplate.refresh(MappingTestEntity.class);

        List<MappingTestEntity> entities = Arrays.asList(
            MappingTestEntity.builder().fieldTypeKeyword("sample text").fieldTypeText("sample text").build()
        );

        mappingTestRepository.saveAll(entities);


        // { "term" : { "field_type_keyword" : "sample" }
        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(new TermQueryBuilder("field_type_keyword", "sample"))
                .build();

        Page<MappingTestEntity> results = mappingTestRepository.search(query);
        assertTrue(results.getTotalElements() == 1L);

        // { "term" : { "field_type_text" : "sample" }
        query = new NativeSearchQueryBuilder()
                .withQuery(new TermQueryBuilder("field_type_text", "sample"))
                .build();

        results = mappingTestRepository.search(query);
        assertTrue(results.hasContent());

        // { "term" : { "field_type_keyword" : "sample text" }
        query = new NativeSearchQueryBuilder()
                .withQuery(new TermQueryBuilder("field_type_keyword", "sample text"))
                .build();

        results = mappingTestRepository.search(query);
        System.out.println("result1 : " + results.getTotalElements());

        // { "match" : { "field_type_text" : "sample text" }
        query = new NativeSearchQueryBuilder()
                .withQuery(new MatchQueryBuilder("field_type_text", "sample text"))
                .build();

        results = mappingTestRepository.search(query);
        System.out.println("result2 : " + results.getTotalElements());
    }
}
