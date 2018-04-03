package org.esdemo.spring;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.esdemo.AbstractTestRunner;
import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;

/**
 * @author zacconding
 * @Date 2018-04-04
 * @GitHub : https://github.com/zacscoding
 */
public class PersistentEntityTest extends AbstractTestRunner {

    @Test
    public void esPersistentEntityTest() {
        ElasticsearchPersistentEntity persistentEntity = getPersistentEntity(PersistentTestEntity.class);
        System.out.println(persistentEntity);
        System.out.println(persistentEntity.getIndexName());
        System.out.println(persistentEntity.getIndexType());
    }

    public ElasticsearchPersistentEntity getPersistentEntity(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Document.class)) {
            System.out.println(clazz.getSimpleName() + " must be annotated");
            return null;
        }

        return elasticsearchTemplate.getElasticsearchConverter().getMappingContext().getRequiredPersistentEntity(clazz);
    }
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Document(indexName = "persistent-test", type="test", shards = 1, replicas = 0, refreshInterval = "-1")
class PersistentTestEntity {
    @Id
    private String id;

    @Field(type = FieldType.Integer)
    private int testField;
}
