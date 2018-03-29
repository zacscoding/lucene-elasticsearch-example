package org.esdemo.aggs;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.esdemo.AbstractTestRunner;
import org.esdemo.entity.Person;
import org.esdemo.entity.ReceiptEntity;
import org.esdemo.entity.elastic.EmptyPage;
import org.esdemo.repository.PersonRepository;
import org.esdemo.util.SimpleLogger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.Arrays;
import java.util.List;

public class TermsAggsTest extends AbstractTestRunner {

    @Autowired
    protected PersonRepository personRepository;

    // FAIL !!! Have to try again!!
    @Test
    public void termsTest() {
        elasticsearchTemplate.deleteIndex(Person.class);
        elasticsearchTemplate.createIndex(Person.class);
        // elasticsearchTemplate.putMapping(Person.class);
        elasticsearchTemplate.refresh(Person.class);

        List<Person> entities = Arrays.asList(
            new Person(null, "name1", 0),
            new Person(null, "name2", 0),
            new Person(null, "name3 name4", 0),
            new Person(null, "name1", 0),
            new Person(null, "name1", 0),
            new Person(null, "name2", 0)
        );
        entities.forEach(p -> {
            personRepository.save(p);
        });
        // personRepository.saveAll(entities);
        System.out.println("@@Success to save..");

        TermsAggregationBuilder aggrBuilder = AggregationBuilders.terms("term_name").field("name");
        SearchQuery query = new NativeSearchQueryBuilder()
            .withIndices("persons")
            .withPageable(EmptyPage.INSTANCE)
            .addAggregation(aggrBuilder)
            .build();

        Aggregations aggr = elasticsearchTemplate.query(query, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse response) {
                return response.getAggregations();
            }
        });

        Terms nameTerms = aggr.get("term_name");
        for (Terms.Bucket entry : nameTerms.getBuckets()) {
            String name = entry.getKeyAsString();
            long docCount = entry.getDocCount();
            SimpleLogger.println("name : {}, doc count : {}", name, docCount);
        }
    }

    // FAIL !!! Have to try again!!
    @Test
    public void multipleFieldsTermsAggsTest() {
        super.clearIndex(ReceiptEntity.class);
        // want to find persons in from or to fields
        List<ReceiptEntity> receipts = Arrays.asList(
            new ReceiptEntity(null, "person1", "person2", 100),
            new ReceiptEntity(null, "person1", "person3", 100),
            new ReceiptEntity(null, "person2", "person1", 100),
            new ReceiptEntity(null, "person2", "person4", 100),
            new ReceiptEntity(null, "person3", "person1", 100)
        );

        super.bulkProcess(receipts);

        TermsAggregationBuilder aggrBuilder = AggregationBuilders.terms("terms_from").field("from");

        SearchQuery query = new NativeSearchQueryBuilder()
            .withPageable(EmptyPage.INSTANCE)
            .addAggregation(aggrBuilder).build();

        Aggregations aggr = elasticsearchTemplate.query(query, response -> {
            SimpleLogger.printJSONPretty(response);
            return response.getAggregations();
        });

    }
}

