package com.estest.repository.es;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.estest.model.withdoc.Book;
import com.estest.repository.es.BookRepository;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/root-context.xml" })
public class PageLearningTest {
	@Resource
	private BookRepository repository;
	@Resource
	private ElasticsearchTemplate tempalte;
	
	@Before
	public void setUp() {
		repository.deleteAll();		
	}
	
	@Test
	public void pageMethodsAPI() {
		final int eltsSize = 101;
		int curPage = 11-1; // must less than last page -1 
		final int listSize = 10;
		List<Book> list = new ArrayList<>(eltsSize);
		for(int i=0; i<eltsSize; i++) {
			Book book = new Book(null,"test"+i,System.currentTimeMillis());
			list.add(book);
		}
		repository.save(list);		
		
		// when		
		Page<Book> books = repository.findAll(new PageRequest(curPage,listSize));
		
		// then
		// total amount of elts
		assertThat(books.getTotalElements(), is((long)eltsSize));
		
		// number of total pages
		assertThat(books.getTotalPages(), is((int)Math.ceil(eltsSize / (double)listSize)));
		
		// number of current Slice
		assertThat(books.getNumber(), is(curPage));
		
		// number of elts currently on this Slice
		int curEltsSize = eltsSize - (curPage*listSize);
		if(curEltsSize > listSize)
			curEltsSize = listSize;
		assertThat(books.getNumberOfElements(), is(curEltsSize));
		
		// size of the Slice
		assertThat(books.getSize(), is(listSize));
		
		// size of the Slice
		assertNull(books.getSort());
		
		// whether the Slice has content at all
		boolean hasContent = books.getTotalPages() > curPage;
		assertTrue(books.hasContent() == hasContent);
		
		// there is a previous Slice
		boolean hasPrevious = curPage == 0 ? false : true;
		assertThat(books.hasPrevious(),is(hasPrevious));
		
		// there is next Slice
		boolean hasNext = (curPage+1) == books.getTotalPages() ? false : true;		
		assertThat(books.hasNext(),is(hasNext));
		
		// whether the current Slice is the first one 
		assertTrue(books.isFirst() != hasPrevious);
		
		// whether the current Slice is the last one
		assertTrue(books.isLast() != hasNext);
		
		/*
		// the Pageable to request the next Slice
		Pageable nextPage = books.nextPageable();
		if(nextPage != null) {
			System.out.println(nextPage.getPageNumber());
			System.out.println(nextPage.getOffset());
			System.out.println(nextPage.getPageSize());			
		}
		System.out.println(nextPage);
		*/
	}
}
