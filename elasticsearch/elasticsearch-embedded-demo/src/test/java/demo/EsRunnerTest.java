package demo;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

/**
 * @author zacconding
 * @Date 2018-11-27
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
public class EsRunnerTest {

    @Test
    public void runningES() throws IOException, InterruptedException {
        EmbeddedElastic embeddedElastic = EmbeddedElastic.builder()
                                                         .withElasticVersion("6.3.0")
                                                         .withSetting(PopularProperties.TRANSPORT_TCP_PORT, 9300)
                                                         .withSetting(PopularProperties.HTTP_PORT, 9200)
                                                         .withEsJavaOpts("-Xms128m -Xmx512m")
                                                         .withStartTimeout(1L, TimeUnit.MINUTES)
                                                         .build()
                                                         .start();

        RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(new HttpHost("127.0.0.1", 9200, "http"))
        );

        MainResponse response = client.info();
        log.info("## Cluster Name : " + response.getClusterName().toString());
        log.info("## Cluster UUID : " + response.getClusterUuid());
        log.info("## Node Name : " + response.getNodeName());
        log.info("## Version : " + response.getVersion());

        embeddedElastic.stop();
    }
}
