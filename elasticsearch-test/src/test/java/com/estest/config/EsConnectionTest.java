package com.estest.config;

import static org.junit.Assert.assertNotNull;

import java.util.Map;

import javax.inject.Inject;

import org.elasticsearch.client.Client;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/root-context.xml" })
public class EsConnectionTest {
	@Inject
	private ElasticsearchTemplate elasticsearchTemplate;
	@Inject
	private Client client;
	
	@Test
	public void basic() {
		assertNotNull(elasticsearchTemplate);
		assertNotNull(client);
	}
	
	@Test
	public void client() {
		System.out.println("=== elastic search ===");
		// Client client = es.getClient();
		Client client = elasticsearchTemplate.getClient();
		Map<String, String> props = client.settings().getAsMap();
		props.forEach((k, v) -> {
			System.out.println(k + " = " + v);
		});
		// executeData("{\"key\":\"value\"}", client);
	}

}
	