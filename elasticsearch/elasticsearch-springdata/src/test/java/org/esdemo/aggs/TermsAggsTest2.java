package org.esdemo.aggs;

import java.util.Arrays;
import java.util.List;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.esdemo.AbstractTestRunner;
import org.esdemo.entity.ReceiptEntity;
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
 * @Date 2018-04-08
 * @GitHub : https://github.com/zacscoding
 */
public class TermsAggsTest2 extends AbstractTestRunner {

    @Autowired
    ReceiptRepository receiptRepository;

    @Before
    public void setUp() {
        super.clearIndex(ReceiptEntity.class);
        // person1 : 1 // person2 : 3 // person3 : 3 // person4 : 1 // person5 : 2
        List<ReceiptEntity> receiptEntities = Arrays.asList(
            ReceiptEntity.builder().from("person1").to("person2").price(1).fromAndTo("person1 person2").build(),
            ReceiptEntity.builder().from("person2").to("person3").price(1).fromAndTo("person2 person3").build(),
            ReceiptEntity.builder().from("person3").to("person4").price(1).fromAndTo("person3 person4").build(),
            ReceiptEntity.builder().from("person3").to("person5").price(1).fromAndTo("person3 person5").build(),
            ReceiptEntity.builder().from("person2").to("person5").price(1).fromAndTo("person2 person5").build()
        );
        receiptRepository.saveAll(receiptEntities);
    }

    @Test
    public void findFromOrToSearch() {
        SimpleLogger.printTitle("Find All");
        receiptRepository.findAll().forEach(e -> System.out.println(e));
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(new TermQueryBuilder("fromAndTo", "person2"))
            .build();
        Page<ReceiptEntity> results = receiptRepository.search(searchQuery);

        SimpleLogger.printTitle("Find Search");
        System.out.println(results.getTotalElements());
        results.getContent().forEach(e -> System.out.println(e));
    }
}
