package org.esdemo.save.mapping;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.esdemo.AbstractTestRunner;
import org.esdemo.entity.MappingTestEntity;
import org.esdemo.repository.MappingTestRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

/**
 * @author zacconding
 * @Date 2018-04-03
 * @GitHub : https://github.com/zacscoding
 */
public class MappingIndexTest extends AbstractTestRunner  {
    @Autowired
    MappingTestRepository mappingTestRepository;

    @Test(expected = SearchPhaseExecutionException.class)
    public void indexFalseAndSearch() {
        super.clearIndex(MappingTestEntity.class);

        List<MappingTestEntity> entities = Arrays.asList(
            MappingTestEntity.builder().fieldTypeKeywordAndNoIndex("keyword").fieldTypeTextAndNoIndex("text").build()
        );
        mappingTestRepository.saveAll(entities);

        SearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(new TermQueryBuilder("fieldTypeKeywordAndNoIndex", "keyword"))
            .build();
        Page<MappingTestEntity> results = mappingTestRepository.search(query);
        fail("exception will occur!!");
    }

    @Test
    public void indexFalseIsSaved() {
        super.clearIndex(MappingTestEntity.class);

        List<MappingTestEntity> entities = Arrays.asList(
            MappingTestEntity.builder().fieldTypeKeyword("kewword").fieldTypeKeywordAndNoIndex("keywordAndNoIndex").fieldTypeTextAndNoIndex("text text").build()
        );
        mappingTestRepository.saveAll(entities);
        Page<MappingTestEntity> results =  mappingTestRepository.findAll(PageRequest.of(0,10));
        assertTrue(results.getTotalElements() == 1L);
        MappingTestEntity find = results.getContent().get(0);
        assertThat(find.getFieldTypeKeywordAndNoIndex(), is("keywordAndNoIndex"));
        assertThat(find.getFieldTypeTextAndNoIndex(), is("text text"));
    }
}
