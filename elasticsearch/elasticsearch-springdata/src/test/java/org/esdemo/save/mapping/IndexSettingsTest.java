package org.esdemo.save.mapping;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import org.esdemo.AbstractTestRunner;
import org.esdemo.entity.SettingsEntity;
import org.esdemo.util.ReflectionUtil;
import org.esdemo.util.SimpleLogger;
import org.junit.Test;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @author zacconding
 * @Date 2018-04-05
 * @GitHub : https://github.com/zacscoding
 */
public class IndexSettingsTest extends AbstractTestRunner {

    @Test
    public void originSettings() {
        /*
        ElasticsearchTemplate::createIndex(class)
            => 1) check @Document
            => 2) check exist == createIfNotExist
            => 3) check settings @Setting == createIndexWithSettings
            => 4) createIndex(class, Object settings == String)
         */
        SimpleLogger.printJSONPretty(elasticsearchTemplate.getSetting(SettingsEntity.class));
        SimpleLogger.printJSONPretty(elasticsearchTemplate.getMapping(SettingsEntity.class));

        ReflectionUtil.modifyAnnotationValue(SettingsEntity.class, Document.class, changeSettings());
        assertTrue(elasticsearchTemplate.deleteIndex(SettingsEntity.class));
        assertFalse(elasticsearchTemplate.indexExists(SettingsEntity.class));

        elasticsearchTemplate.createIndex(SettingsEntity.class);
        elasticsearchTemplate.putMapping(SettingsEntity.class);
        elasticsearchTemplate.refresh(SettingsEntity.class);

        System.out.println("index exists : " + elasticsearchTemplate.indexExists(SettingsEntity.class));

        SimpleLogger.printJSONPretty(elasticsearchTemplate.getSetting(SettingsEntity.class));
        SimpleLogger.printJSONPretty(elasticsearchTemplate.getMapping(SettingsEntity.class));
    }

    @Test
    public void createIndexSettingsIfNotExist() {
        elasticsearchTemplate.deleteIndex(SettingsEntity.class);
        elasticsearchTemplate.createIndex(SettingsEntity.class);
        SimpleLogger.build().println("## == Origin == ##");
        SimpleLogger.printJSONPretty(elasticsearchTemplate.getSetting(SettingsEntity.class));

        elasticsearchTemplate.deleteIndex(SettingsEntity.class);
        elasticsearchTemplate.createIndexSettingsIfNotExist(SettingsEntity.class);

        SimpleLogger.build().println("## == Changed == ##");
        SimpleLogger.printJSONPretty(elasticsearchTemplate.getSetting(SettingsEntity.class));
    }

    public Document changeSettings() {
        Document origin = SettingsEntity.class.getAnnotation(Document.class);

        return new Document() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return origin.annotationType();
            }

            @Override
            public String indexName() {
                return origin.indexName();
            }

            @Override
            public String type() {
                return origin.type();
            }

            @Override
            public boolean useServerConfiguration() {
                return origin.useServerConfiguration();
            }

            @Override
            public short shards() {
                return 3;
            }

            @Override
            public short replicas() {
                return 1;
            }

            @Override
            public String refreshInterval() {
                return "1s";
            }

            @Override
            public String indexStoreType() {
                return origin.indexStoreType();
            }

            @Override
            public boolean createIndex() {
                return origin.createIndex();
            }
        };

    }
}
