package org.esdemo.save;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.elasticsearch.action.DocWriteRequest.OpType;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.index.engine.VersionConflictEngineException;
import org.esdemo.AbstractTestRunner;
import org.esdemo.util.SimpleLogger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

/**
 * @author zacconding
 * @Date 2018-04-06
 * @GitHub : https://github.com/zacscoding
 */

public class IndexRequestTest extends AbstractTestRunner {

    String indexName;
    String type;

    @Before
    public void setUp() {
        ElasticsearchPersistentEntity<IndexRequestTestEntity> persistentEntity = elasticsearchTemplate
            .getPersistentEntityFor(IndexRequestTestEntity.class);
        indexName = persistentEntity.getIndexName();
        type = persistentEntity.getIndexType();
    }


    @Test
    public void duplicate() throws Exception {
        super.clearIndex(IndexRequestTestEntity.class);
        ObjectMapper mapper = new ObjectMapper();

        try {
            IndexResponse response1 = elasticsearchTemplate.getClient().prepareIndex(indexName, type).setId("1")
                                                           .setSource(mapper.writeValueAsBytes(BulkRequestEntity.builder().testField(200).build()))
                                                           .setOpType(OpType.CREATE).get();

            SimpleLogger.println("## ============================================================ ##");
            System.out.println(response1.status().getStatus());
            SimpleLogger.printJSONPretty(response1);
            SimpleLogger.println("## ============================================================ ##");

            IndexResponse response2 = elasticsearchTemplate.getClient().prepareIndex(indexName, type).setId("1")
                                                           .setSource(mapper.writeValueAsBytes(BulkRequestEntity.builder().testField(100).build()))
                                                           .setOpType(OpType.CREATE).get();

            SimpleLogger.println("## ============================================================ ##");
            SimpleLogger.printJSONPretty(response2);
            SimpleLogger.println("## ============================================================ ##");
        } catch (VersionConflictEngineException e) {
            System.out.println("VersionConflictEngineException occur!!" + e.status().getStatus());
        } catch (Exception e) {
            System.out.println("Exception occur!!" + e.getClass().getName());
        }

        GetQuery getQuery = new GetQuery();
        getQuery.setId("1");
        IndexRequestTestEntity e = elasticsearchTemplate.queryForObject(getQuery, IndexRequestTestEntity.class);
        System.out.println(e);
    }

    @Test
    public void duplicateByIndexQuery() throws Exception {
        super.clearIndex(IndexRequestTestEntity.class);
        try {
            IndexQuery q1 = new IndexQuery();
            q1.setIndexName(indexName);
            q1.setType(type);
            q1.setObject(BulkRequestEntity.builder().testField(200).build());
            q1.setId("1");
            String id = elasticsearchTemplate.index(q1);

            SimpleLogger.println("## ============================================================ ##");
            SimpleLogger.printJSONPretty(id);
            SimpleLogger.println("## ============================================================ ##");

            IndexQuery q2 = new IndexQuery();
            q2.setIndexName(indexName);
            q2.setType(type);
            q2.setObject(BulkRequestEntity.builder().testField(100).build());

            q2.setId("1");
            String id2 = elasticsearchTemplate.index(q2);
            SimpleLogger.println("## ============================================================ ##");
            SimpleLogger.printJSONPretty(id2);
            SimpleLogger.println("## ============================================================ ##");
        } catch (VersionConflictEngineException e) {
            System.out.println("VersionConflictEngineException occur!!" + e.status().getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception occur!!" + e.getClass().getName());
        }

        GetQuery getQuery = new GetQuery();
        getQuery.setId("1");
        IndexRequestTestEntity e = elasticsearchTemplate.queryForObject(getQuery, IndexRequestTestEntity.class);
        System.out.println(e);
    }
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Document(indexName = "index-request-test", type = "test", shards = 1, replicas = 0, refreshInterval = "-1")
@JsonIgnoreProperties({"id"})
class IndexRequestTestEntity {

    @Id
    private String id;

    @Field(type = FieldType.Integer)
    private int testField;
}