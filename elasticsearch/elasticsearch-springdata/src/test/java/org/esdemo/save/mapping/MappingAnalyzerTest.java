package org.esdemo.save.mapping;

import org.elasticsearch.index.query.TermQueryBuilder;
import org.esdemo.AbstractTestRunner;
import org.esdemo.entity.MappingTestEntity;
import org.esdemo.repository.MappingTestRepository;
import org.esdemo.util.SimpleLogger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-analyzers.html
 *
 * @author zacconding
 * @Date 2018-04-08
 * @GitHub : https://github.com/zacscoding
 */
public class MappingAnalyzerTest extends AbstractTestRunner {

    private static boolean init = false;
    @Autowired
    MappingTestRepository mappingTestRepository;

    @Before
    public void setUp() {
        if (init) {
            super.clearIndex(MappingTestEntity.class);
            String text = "The 2 QUICK Brown-Foxes jumped over the lazy dog's bone.";
            MappingTestEntity e = MappingTestEntity.builder()
                                                   .defaultAnalyzer(text)
                                                   .simpleAnalyzer(text)
                                                   .whitespaceAnalyzer(text)
                                                   .stopAnalyzer(text)
                                                   .keywordAnalyzer(text)
                                                   .patternAnalyzer(text)
                                                   .build();
            mappingTestRepository.save(e);
            init = true;
        }
    }

    @Test
    public void test() {
        super.clearIndex(MappingTestEntity.class);
    }

    @Test
    public void defaultAnalyzer() {
        /**
         * Standard Analyzer
         * [ the, 2, quick, brown, foxes, jumped, over, the, lazy, dog's, bone ]
         */
        final String defaultField = "defaultAnalyzer";
        String[] defaultTerms = {
            "the", "2", "quick", "brown", "foxes", "jumped", "over", "the", "lazy", "dog's", "bone"
        };
        test("Test Default Analyzer", defaultField, defaultTerms);

        /**
         * Simple Analyzer
         *
         * [ the, quick, brown, foxes, jumped, over, the, lazy, dog, s, bone ]
         */
        final String simpleField = "simpleAnalyzer";
        String[] simpleTerms = {
            "the", "2", "quick", "brown", "foxes", "jumped", "over", "the", "lazy", "dog's", "bone"
        };
        test("Test Simple Analyzer", simpleField, simpleTerms);



    }

    private void test(String title, String field, String[] termValues) {
        SimpleLogger.printTitle(title);
        for (String value : termValues) {
            Page<MappingTestEntity> entities = findByTerm(field, value);
            SimpleLogger.println("search type : {}, value : {} , result : {}", field, value, entities.getTotalElements());
        }
        SimpleLogger.printTitle("====================");
    }

    public Page<MappingTestEntity> findByTerm(String field, String value) {
        return mappingTestRepository.search(
            new NativeSearchQueryBuilder()
                .withQuery(new TermQueryBuilder(field, value))
                .withPageable(PageRequest.of(0, 20))
                .build()
        );
    }


}
