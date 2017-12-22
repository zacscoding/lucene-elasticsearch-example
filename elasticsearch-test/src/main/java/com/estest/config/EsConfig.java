package com.estest.config;

import java.net.InetAddress;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;


@Configuration
@PropertySource(value = "classpath:properties/elasticsearch.properties")
@EnableElasticsearchRepositories(basePackages = { "com.estest.repository.es" })
public class EsConfig {	
	@Value("${elasticsearch.host}")
	private String EsHost;

	@Value("${elasticsearch.port}")
	private int EsPort;

	@Value("${elasticsearch.clustername}")
	private String EsClusterName;

	/* for ES 5.x */
	// public Client client2() throws Exception {
	// Settings esSettings = Settings.builder().put("cluster.name",
	// EsClusterName).build();
	// return new PreBuiltTransportClient(esSettings)
	// .addTransportAddress(new
	// InetSocketTransportAddress(InetAddress.getByName(EsHost), EsPort));
	// }

	//////////////////////////////////
	//
	//////////////////////////////////
	@Bean
	public Client client() throws Exception {
		Settings esSettings = Settings.settingsBuilder().put("cluster.name", EsClusterName).build();
		// https://www.elastic.co/guide/en/elasticsearch/guide/current/_transport_client_versus_node_client.html
		return TransportClient.builder().settings(esSettings).build()
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(EsHost), EsPort));
	}

	@Bean
	public ElasticsearchTemplate elasticsearchTemplate() throws Exception {
		// return new ElasticsearchTemplate(client());
		return new ElasticsearchTemplate(client());
	}

}
