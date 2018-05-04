package org.esdemo.configuration;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zacconding
 * @Date 2018-05-04
 * @GitHub : https://github.com/zacscoding
 */

@Configuration
public class JestConfiguration {

    @Bean
    public JestClientFactory jestClientFactory() {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(
            new HttpClientConfig.Builder("http://localhost:9200")
                                .multiThreaded(true)
                                //Per default this implementation will create no more than 2 concurrent connections per given route
                                .defaultMaxTotalConnectionPerRoute(5) // < YOUR_DESIRED_LEVEL_OF_CONCURRENCY_PER_ROUTE >
                                // and no more 20 connections in total
                                .maxTotalConnection(10) // < YOUR_DESIRED_LEVEL_OF_CONCURRENCY_TOTAL >
                                .build());

        return factory;
    }
}
