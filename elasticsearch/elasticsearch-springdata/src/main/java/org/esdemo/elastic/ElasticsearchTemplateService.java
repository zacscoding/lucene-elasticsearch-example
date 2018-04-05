package org.esdemo.elastic;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.core.ResultsMapper;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.stereotype.Service;

@Service
public class ElasticsearchTemplateService extends ElasticsearchTemplate {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchTemplateService.class);

    @Autowired
    public ElasticsearchTemplateService(Client client) {
        super(client);
    }

    public ElasticsearchTemplateService(Client client, EntityMapper entityMapper) {
        super(client, entityMapper);
    }

    public ElasticsearchTemplateService(Client client, ElasticsearchConverter elasticsearchConverter, EntityMapper entityMapper) {
        super(client, elasticsearchConverter, entityMapper);
    }

    public ElasticsearchTemplateService(Client client, ResultsMapper resultsMapper) {
        super(client, resultsMapper);
    }

    public ElasticsearchTemplateService(Client client, ElasticsearchConverter elasticsearchConverter) {
        super(client, elasticsearchConverter);
    }

    public ElasticsearchTemplateService(Client client, ElasticsearchConverter elasticsearchConverter, ResultsMapper resultsMapper) {
        super(client, elasticsearchConverter, resultsMapper);
    }

    public boolean createIndexSettingsIfNotExist(Class<?> clazz) {
        if (super.indexExists(clazz)) {
            return true;
        }

        ElasticsearchPersistentEntity persistentEntity = super.getPersistentEntityFor(clazz);
        String indexName = persistentEntity.getIndexName();
        return super.getClient().admin().indices().prepareCreate(indexName)
                     .setSettings(Settings.builder()
                                          .put("number_of_shards", persistentEntity.getShards())
                                          .put("number_of_replicas", persistentEntity.getReplicas())
                                          .put("refresh_interval", persistentEntity.getRefreshInterval())
                                          .put("index.store.type", persistentEntity.getIndexStoreType()))
                     .get().isShardsAcked();
    }
}
