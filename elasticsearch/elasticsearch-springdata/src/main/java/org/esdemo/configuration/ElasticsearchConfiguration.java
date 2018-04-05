package org.esdemo.configuration;

import org.elasticsearch.client.Client;
import org.esdemo.elastic.ElasticsearchTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

/**
 * @author zacconding
 * @Date 2018-04-05
 * @GitHub : https://github.com/zacscoding
 */
@Configuration
public class ElasticsearchConfiguration {

    @Autowired
    Client client;

    @Bean
    public ElasticsearchTemplate elasticsearchTemplate() {
        return new ElasticsearchTemplateService(client);
    }
}
