package com.estest.repository.es;

import com.estest.model.withdoc.SimpleBook;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SimpleBookRepository extends ElasticsearchRepository<SimpleBook,String> {

}
