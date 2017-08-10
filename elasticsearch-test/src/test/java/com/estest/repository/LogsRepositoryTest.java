package com.estest.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.estest.model.Logs;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml" })
public class LogsRepositoryTest {
	@Autowired
	ElasticsearchTemplate elasticsearchTemplate;
	ObjectMapper mapper;
	private String[] sampleIndexes;
	
	@Before 
	public void setUp() {
		sampleIndexes = new String[]{"20170809","20170810"};
		
		for(String index : sampleIndexes) {
			elasticsearchTemplate.deleteIndex(index);
		}
		
		mapper = new ObjectMapper();
	}
	
	@Test
	public void save() throws Exception {
		Logs log1 = new Logs();
		log1.setLogContent("0809 logs");
		log1.setTime(getDate(sampleIndexes[0]));
		
		Logs log2 = new Logs();
		log2.setLogContent("0810 logs");
		log2.setTime(getDate(sampleIndexes[1]));
		
		IndexQuery indexQuery = new IndexQuery();
		indexQuery.setIndexName(sampleIndexes[0]);
		indexQuery.setType("log");
		//indexQuery.setObject(log1);
		indexQuery.setSource(log1.toString());
		
		
		String id1 = elasticsearchTemplate.index(indexQuery);
		
		IndexQuery indexQuery2 = new IndexQuery();
		indexQuery2.setIndexName(sampleIndexes[1]);
		indexQuery2.setType("log");
		indexQuery2.setSource(log2.toString());
		
		String id2 = elasticsearchTemplate.index(indexQuery2);
		
		GetQuery getQuery = new GetQuery();
		getQuery.setId(id1);
		Logs find1 = elasticsearchTemplate.queryForObject(getQuery, Logs.class);
		System.out.println(find1.toString());
		assertThat(find1.toString(),is(log1.toString()));
		getQuery.setId(id2);
		
		Logs find2 = elasticsearchTemplate.queryForObject(getQuery, Logs.class);
		System.out.println(find2.toString());
		assertThat(find2.toString(),is(log2.toString()));
		
	}
	
	private Date getDate(String dateVal) {
		int year = Integer.parseInt(dateVal.substring(0,4));
		int month = Integer.parseInt(dateVal.substring(4,6));
		int day = Integer.parseInt(dateVal.substring(6));
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONDAY, month);
		cal.set(Calendar.DATE, day);
		
		return cal.getTime();
	}
	
	
	
	

}
