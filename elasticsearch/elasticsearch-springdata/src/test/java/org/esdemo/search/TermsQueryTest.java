package org.esdemo.search;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregator;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.esdemo.AbstractTestRunner;
import org.esdemo.entity.ReceiptEntity;
import org.esdemo.entity.elastic.EmptyPage;
import org.esdemo.repository.ReceiptRepository;
import org.esdemo.util.SimpleLogger;
import org.junit.Before;
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
public class TermsQueryTest extends AbstractTestRunner {

    private static boolean init = true;

    @Autowired
    ReceiptRepository receiptRepository;

    @Before
    public void setUp() {
        if (!init) {
            super.clearIndex(ReceiptEntity.class);
            // person1 : 1 // person2 : 3 // person3 : 3 // person4 : 1 // person5 : 2
            List<ReceiptEntity> receiptEntities = Arrays.asList(
                new ReceiptEntity(null, "person1", "person2", 1),
                new ReceiptEntity(null, "person2", "person3", 1),
                new ReceiptEntity(null, "person3", "person4", 1),
                new ReceiptEntity(null, "person3", "person5", 1),
                new ReceiptEntity(null, "person5", "person2", 1),
                new ReceiptEntity(null, "0xdacc9c61754a0c4616fc5323dc946e89eb272302 0x682b7903a11098cf770c7aef4aa02a85b3f3601a", null, 1)
            );

            receiptRepository.saveAll(receiptEntities);
            init = true;
            SimpleLogger.println("## Initialized ReceiptEntity index..");
        }
    }

    @Test
    public void termsWithArrays() {
        String[] terms = {"person1", "person2", "person5"};
        SearchQuery query = new NativeSearchQueryBuilder().withQuery(new TermsQueryBuilder("from", terms)).build();
        Page<ReceiptEntity> entity = elasticsearchTemplate.queryForPage(query, ReceiptEntity.class);
        assertTrue(entity.getTotalElements() == 3L);

        List<String> termsList = Arrays.asList(terms);
        query = new NativeSearchQueryBuilder().withQuery(new TermsQueryBuilder("from", terms)).build();
        entity = elasticsearchTemplate.queryForPage(query, ReceiptEntity.class);
        assertTrue(entity.getTotalElements() == 3L);
    }

    @Test
    public void termsAndShould() {
        List<String> terms = Arrays.asList("person1", "person2");
        QueryBuilder queryBuilder = new BoolQueryBuilder().should(new TermsQueryBuilder("from", terms)).should(new TermsQueryBuilder("to", terms));
        SearchQuery query = new NativeSearchQueryBuilder().withQuery(queryBuilder).build();
        Page<ReceiptEntity> entity = elasticsearchTemplate.queryForPage(query, ReceiptEntity.class);
        assertTrue(entity.getTotalElements() == 3L);
    }

/*
{
	"size" : 0,
	"aggs" : {
		"FROM_FILTER" : {
			"filter" : {
				"terms" : { "from" : ["person1", "person2"] }
			},
			"aggs" : {
				"TERM_FROM" : {
					"terms" : {
						"field" : "from"
					},
					"aggs" : {
						"TO_FILTER" : {
							"filter" : {
								"terms" : { "from" : ["person1", "person2"] }
							},
							"aggs" : {
								"TERM_TO" : {
									"terms" : {
										"field" : "to"
									}
								}
							}
						}
					}
				}
			}
		}
    }
}
 */
    @Test
    public void termsAggs() {
        List<String> terms = Arrays.asList("person1", "person2");
        FilterAggregationBuilder fromFilter = AggregationBuilders.filter("FILTER_FROM", new TermsQueryBuilder("from", terms))
                                                                .subAggregation(AggregationBuilders.terms("TERM_FROM").field("from").size(terms.size()));

        FilterAggregationBuilder toFilter = AggregationBuilders.filter("FILTER_TO", new TermsQueryBuilder("to", terms))
                                                               .subAggregation(AggregationBuilders.terms("TERM_TO").field("to").size(terms.size()));

        SearchQuery query = new NativeSearchQueryBuilder().withPageable(EmptyPage.INSTANCE)
                                                          .addAggregation(fromFilter)
                                                          .addAggregation(toFilter)
                                                          .build();

        Aggregations aggs = elasticsearchTemplate.query(query, res -> {
            SimpleLogger.printPrettyFromJsonValue(res.toString());
            return res.getAggregations();
        });


        long person1Cnt = 0L, person2Cnt = 0L;
        InternalFilter fromFilterAggs = aggs.get("FILTER_FROM");
        InternalFilter toFilterAggs = aggs.get("FILTER_TO");

        Terms fromTerms = fromFilterAggs.getAggregations().get("TERM_FROM");
        for (Terms.Bucket entry : fromTerms.getBuckets()) {
            long docCount = entry.getDocCount();
            if ("person1".equals(entry.getKey())) {
                person1Cnt += docCount;
            } else if ("person2".equals(entry.getKey())) {
                person2Cnt += docCount;
            } else {
                fail("Not person1 or person2, key : " + entry.getKey());
            }
        }

        Terms toTerms = toFilterAggs.getAggregations().get("TERM_TO");
        for(Terms.Bucket entry : toTerms.getBuckets()) {
            long docCount = entry.getDocCount();
            if ("person1".equals(entry.getKey())) {
                person1Cnt += docCount;
            } else if ("person2".equals(entry.getKey())) {
                person2Cnt += docCount;
            } else {
                fail("Not person1 or person2, key : " + entry.getKey());
            }
        }

        assertTrue(person1Cnt == 1L);
        assertTrue(person2Cnt == 3L);
    }
}
