package com.estest.repository.es;

import com.estest.model.index.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookRepository extends ElasticsearchRepository<Book,String> {
	Page<Book> findByNameAndPrice(String name, Long price, Pageable pageable);
    Page<Book> findByNameOrPrice(String name, Long price, Pageable pageable);
    Page<Book> findByName(String name, Pageable pageable);
}
