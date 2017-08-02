package com.estest.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.estest.model.index.Book;
import com.estest.repository.es.BookRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/root-context.xml" })
public class BookRepositoryTest {
	@Resource
	private BookRepository repository;
	@Resource
	private ElasticsearchTemplate tempalte;

	@Before
	public void setUp() {
		repository.deleteAll();
	}

	// save and search
	@Test
	public void shouldIndexSingleBookEntity() {
		Book book = new Book();
		book.setId("12345");
		book.setName("Spring data ES");
		book.setVersion(System.currentTimeMillis());

		// check save
		Book saved = repository.save(book);
		assertThat(book.getId(), is(saved.getId()));
		assertThat(book.getName(), is(saved.getName()));

		// check search
		Book indexBook = repository.findOne(book.getId());
		assertNotNull(indexBook);
		assertThat(book.getId(), is(indexBook.getId()));
		assertThat(book.getName(), is(indexBook.getName()));
	}

	// multiple save and search
	@Test
	public void shouldBulkIndexMultipleBookEntities() {
		Book book1 = new Book(RandomStringUtils.random(5), "Spring Data", System.currentTimeMillis());
		Book book2 = new Book(RandomStringUtils.random(5), "Spring Data ES", System.currentTimeMillis());

		// Bulk Index
		repository.save(Arrays.asList(book1, book2));

		// Search
		Book indexBook1 = repository.findOne(book1.getId());
		assertThat(book1.getId(), is(indexBook1.getId()));
		assertThat(book1.getName(), is(indexBook1.getName()));

		Book indexBook2 = repository.findOne(book2.getId());
		assertThat(book2.getId(), is(indexBook2.getId()));
		assertThat(book2.getName(), is(indexBook2.getName()));
	}
	
	 @Test
	 public void shouldCountAllElementsInIndex() {
		 List<Book> books = new ArrayList<>();
		 Queue<String> que = new LinkedList<>();
		 
		 for(int i=1; i<=10; i++) {
			 String bookName = "Spring Data Books"+i;
			 books.add(new Book(null,bookName, System.currentTimeMillis()));
			 que.offer(bookName);
		 }
		 
		 // maintain order?
		 repository.save(books).forEach(k-> {
			 //System.out.println(k.getName());		
			 assertThat(que.poll(), is(k.getName()));
		 }); 
		 
		 long count = repository.count();
		 assertThat(count,is(10L));
	 }
	 
	 @Test
	 public void shouldExecuteCustomSearchQueries() {
		 Book book1 = new Book(null,"Custom Query",System.currentTimeMillis());
		 Book book2 = new Book(null,null,System.currentTimeMillis());
		 
		 // save
		 repository.save(Arrays.asList(book1,book2));
		 
		 SearchQuery query = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchAllQuery())
				 			.withFilter(QueryBuilders.boolQuery().must(QueryBuilders.existsQuery("name")))
				 			.withPageable(new PageRequest(0,10))
				 			.build();
		 
		 Page<Book> books = repository.search(query);
		 assertThat(books.getTotalElements(), is(1L));
	 }
	
	

	@Test
	@Ignore("not to run as just for showing usage of repository ! might throw java.lang.OutOfMemoryError :-) ")
	public void crudRepositoryTest() {

		Book book1 = new Book(RandomStringUtils.random(5), "Spring Data", System.currentTimeMillis());
		Book book2 = new Book(RandomStringUtils.random(5), "Spring Data Elasticsearch", System.currentTimeMillis());
		List<Book> books = Arrays.asList(book1, book2);

		// indexing single document
		repository.save(book1);
		// bulk indexing multiple documents
		repository.save(books);
		// searching single document based on documentId
		Book book = repository.findOne(book1.getId());
		// to get all records as iteratable collection
		Iterable<Book> bookList = repository.findAll();
		// page request which will give first 10 document
		Page<Book> bookPage = repository.findAll(new PageRequest(0, 10));
		// to get all records as ASC on name field
		Iterable<Book> bookIterable = repository.findAll(new Sort(new Sort.Order(Sort.Direction.ASC, "name")));
		// to get total number of docoments in an index
		Long count = repository.count();
		// to check wheather document exists or not
		boolean exists = repository.exists(book1.getId());
		// delete a document by entity
		repository.delete(book1);
		// delete multiple document using collection
		repository.delete(books);
		// delete a document using documentId
		repository.delete(book1.getId());
		// delete all document
		repository.deleteAll();
	}

}
