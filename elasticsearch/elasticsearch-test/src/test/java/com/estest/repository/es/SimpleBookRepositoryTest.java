package com.estest.repository.es;


import com.estest.model.withdoc.SimpleBook;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/root-context.xml" })
public class SimpleBookRepositoryTest {
    @Autowired
    SimpleBookRepository repository;
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;
    
    @Before
    public void setUp() {
                
    }    
    
    @Ignore
    @Test
    public void update() {
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source("new_author","Zac");
        UpdateQuery updateQuery = new UpdateQueryBuilder().withId("1").withClass(SimpleBook.class).withIndexRequest(indexRequest).build();        
        UpdateResponse update = elasticsearchTemplate.update(updateQuery);
        System.out.println(update);        
    }
    
    @Test
    public void test() {
        repository.findAll().forEach(k->{
            System.out.println(k);
        });
    }
    

}


















