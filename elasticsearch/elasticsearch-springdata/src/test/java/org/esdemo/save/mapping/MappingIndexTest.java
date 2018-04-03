package org.esdemo.save.mapping;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.esdemo.AbstractTestRunner;
import org.esdemo.entity.MappingTestEntity;
import org.esdemo.repository.MappingTestRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    // test : FieldType(index = true or false)
    @Test
    public void noIndexTest() {
        // super.clearIndex(MappingTestEntity.class);
        List<MappingTestEntity> entities = new ArrayList<>();
        entities.add(MappingTestEntity.builder().fieldTypeKeyword("test1").fieldTypeKeywordAndNoIndex("test1").build());
        entities.add(MappingTestEntity.builder().fieldTypeKeyword("test1 test2").fieldTypeKeywordAndNoIndex("test1 test2").build());
        entities.add(MappingTestEntity.builder().fieldTypeKeyword("test1 test3").fieldTypeKeywordAndNoIndex("test1 test3").build());
         //super.bulkProcess(entities);

        SearchQuery query = new NativeSearchQueryBuilder().withQuery(new TermQueryBuilder("fieldTypeKeyword","test1")).build();
        Page<MappingTestEntity> result = mappingTestRepository.search(query);
        System.out.println(result.getTotalElements());
        // assertTrue(result.getTotalElements() == 3L);

        query = new NativeSearchQueryBuilder().withQuery(new TermQueryBuilder("fieldTypeKeywordAndNoIndex","test1")).build();
        try {
            result = mappingTestRepository.search(query);
            fail("Exception occur!!");
        } catch(Exception e) {

        }
    }
}
