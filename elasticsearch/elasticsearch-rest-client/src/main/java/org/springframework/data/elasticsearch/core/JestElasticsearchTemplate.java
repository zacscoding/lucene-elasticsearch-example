//package org.springframework.data.elasticsearch.core;
//
//import io.searchbox.action.Action;
//import io.searchbox.client.JestClient;
//import io.searchbox.client.JestResult;
//import io.searchbox.indices.CreateIndex;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import org.apache.commons.lang.StringUtils;
//import org.elasticsearch.action.update.UpdateResponse;
//import org.elasticsearch.client.Client;
//import org.elasticsearch.cluster.metadata.AliasMetaData;
//import org.elasticsearch.common.collect.MapBuilder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.data.domain.Page;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Setting;
//import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
//import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
//import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
//import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
//import org.springframework.data.elasticsearch.core.query.AliasQuery;
//import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
//import org.springframework.data.elasticsearch.core.query.DeleteQuery;
//import org.springframework.data.elasticsearch.core.query.GetQuery;
//import org.springframework.data.elasticsearch.core.query.IndexQuery;
//import org.springframework.data.elasticsearch.core.query.MoreLikeThisQuery;
//import org.springframework.data.elasticsearch.core.query.SearchQuery;
//import org.springframework.data.elasticsearch.core.query.StringQuery;
//import org.springframework.data.elasticsearch.core.query.UpdateQuery;
//import org.springframework.data.util.CloseableIterator;
//import org.springframework.util.Assert;
//
///**
// * Try to change TransportClient => JestClient
// */
//public class JestElasticsearchTemplate implements ElasticsearchOperations {
//    private static final Logger logger = LoggerFactory.getLogger(JestElasticsearchTemplate.class);
//    private JestClient client;
//    private ElasticsearchConverter elasticsearchConverter;
//
//    public JestElasticsearchTemplate(JestClient client) {
//        this.client = client;
//        this.elasticsearchConverter = new MappingElasticsearchConverter(new SimpleElasticsearchMappingContext());
//    }
//
//    @Override
//    public <T> boolean createIndex(Class<T> clazz) {
//        return createIndexIfNotCreated(clazz);
//    }
//
//    @Override
//    public boolean createIndex(String indexName) {
//        Assert.notNull(indexName, "No index defined for Query");
//        return doExecute(new CreateIndex.Builder(indexName).build()).isSucceeded();
//    }
//
//    @Override
//    public boolean createIndex(String indexName, Object settings) {
//        return false;
//    }
//
//    @Override
//    public <T> boolean createIndex(Class<T> clazz, Object settings) {
//        return createIndex(getPersistentEntityFor(clazz).getIndexName(), settings);
//    }
//
//    @Override
//    public <T> boolean putMapping(Class<T> aClass) {
//        return false;
//    }
//
//    @Override
//    public boolean putMapping(String s, String s1, Object o) {
//        return false;
//    }
//
//    @Override
//    public <T> boolean putMapping(Class<T> aClass, Object o) {
//        return false;
//    }
//
//    @Override
//    public <T> Map getMapping(Class<T> aClass) {
//        return null;
//    }
//
//    @Override
//    public Map getMapping(String s, String s1) {
//        return null;
//    }
//
//    @Override
//    public Map getSetting(String s) {
//        return null;
//    }
//
//    @Override
//    public <T> Map getSetting(Class<T> aClass) {
//        return null;
//    }
//
//    @Override
//    public <T> T queryForObject(GetQuery getQuery, Class<T> aClass) {
//        return null;
//    }
//
//    @Override
//    public <T> T queryForObject(GetQuery getQuery, Class<T> aClass, GetResultMapper getResultMapper) {
//        return null;
//    }
//
//    @Override
//    public <T> T queryForObject(CriteriaQuery criteriaQuery, Class<T> aClass) {
//        return null;
//    }
//
//    @Override
//    public <T> T queryForObject(StringQuery stringQuery, Class<T> aClass) {
//        return null;
//    }
//
//    @Override
//    public <T> Page<T> queryForPage(SearchQuery searchQuery, Class<T> aClass) {
//        return null;
//    }
//
//    @Override
//    public <T> Page<T> queryForPage(SearchQuery searchQuery, Class<T> aClass, SearchResultMapper searchResultMapper) {
//        return null;
//    }
//
//    @Override
//    public <T> Page<T> queryForPage(CriteriaQuery criteriaQuery, Class<T> aClass) {
//        return null;
//    }
//
//    @Override
//    public <T> Page<T> queryForPage(StringQuery stringQuery, Class<T> aClass) {
//        return null;
//    }
//
//    @Override
//    public <T> Page<T> queryForPage(StringQuery stringQuery, Class<T> aClass, SearchResultMapper searchResultMapper) {
//        return null;
//    }
//
//    @Override
//    public <T> CloseableIterator<T> stream(CriteriaQuery criteriaQuery, Class<T> aClass) {
//        return null;
//    }
//
//    @Override
//    public <T> CloseableIterator<T> stream(SearchQuery searchQuery, Class<T> aClass) {
//        return null;
//    }
//
//    @Override
//    public <T> CloseableIterator<T> stream(SearchQuery searchQuery, Class<T> aClass, SearchResultMapper searchResultMapper) {
//        return null;
//    }
//
//    @Override
//    public <T> List<T> queryForList(CriteriaQuery criteriaQuery, Class<T> aClass) {
//        return null;
//    }
//
//    @Override
//    public <T> List<T> queryForList(StringQuery stringQuery, Class<T> aClass) {
//        return null;
//    }
//
//    @Override
//    public <T> List<T> queryForList(SearchQuery searchQuery, Class<T> aClass) {
//        return null;
//    }
//
//    @Override
//    public <T> List<String> queryForIds(SearchQuery searchQuery) {
//        return null;
//    }
//
//    @Override
//    public <T> long count(CriteriaQuery criteriaQuery, Class<T> aClass) {
//        return 0;
//    }
//
//    @Override
//    public <T> long count(CriteriaQuery criteriaQuery) {
//        return 0;
//    }
//
//    @Override
//    public <T> long count(SearchQuery searchQuery, Class<T> aClass) {
//        return 0;
//    }
//
//    @Override
//    public <T> long count(SearchQuery searchQuery) {
//        return 0;
//    }
//
//    @Override
//    public <T> LinkedList<T> multiGet(SearchQuery searchQuery, Class<T> aClass) {
//        return null;
//    }
//
//    @Override
//    public <T> LinkedList<T> multiGet(SearchQuery searchQuery, Class<T> aClass, MultiGetResultMapper multiGetResultMapper) {
//        return null;
//    }
//
//    @Override
//    public String index(IndexQuery indexQuery) {
//        return null;
//    }
//
//    @Override
//    public UpdateResponse update(UpdateQuery updateQuery) {
//        return null;
//    }
//
//    @Override
//    public void bulkIndex(List<IndexQuery> list) {
//
//    }
//
//    @Override
//    public void bulkUpdate(List<UpdateQuery> list) {
//
//    }
//
//    @Override
//    public String delete(String s, String s1, String s2) {
//        return null;
//    }
//
//    @Override
//    public <T> void delete(CriteriaQuery criteriaQuery, Class<T> aClass) {
//
//    }
//
//    @Override
//    public <T> String delete(Class<T> aClass, String s) {
//        return null;
//    }
//
//    @Override
//    public <T> void delete(DeleteQuery deleteQuery, Class<T> aClass) {
//
//    }
//
//    @Override
//    public void delete(DeleteQuery deleteQuery) {
//
//    }
//
//    @Override
//    public <T> boolean deleteIndex(Class<T> aClass) {
//        return false;
//    }
//
//    @Override
//    public boolean deleteIndex(String s) {
//        return false;
//    }
//
//    @Override
//    public <T> boolean indexExists(Class<T> aClass) {
//        return false;
//    }
//
//    @Override
//    public boolean indexExists(String s) {
//        return false;
//    }
//
//    @Override
//    public boolean typeExists(String s, String s1) {
//        return false;
//    }
//
//    @Override
//    public void refresh(String s) {
//
//    }
//
//    @Override
//    public <T> void refresh(Class<T> aClass) {
//
//    }
//
//    @Override
//    public <T> Page<T> startScroll(long l, SearchQuery searchQuery, Class<T> aClass) {
//        return null;
//    }
//
//    @Override
//    public <T> Page<T> startScroll(long l, SearchQuery searchQuery, Class<T> aClass, SearchResultMapper searchResultMapper) {
//        return null;
//    }
//
//    @Override
//    public <T> Page<T> startScroll(long l, CriteriaQuery criteriaQuery, Class<T> aClass) {
//        return null;
//    }
//
//    @Override
//    public <T> Page<T> startScroll(long l, CriteriaQuery criteriaQuery, Class<T> aClass, SearchResultMapper searchResultMapper) {
//        return null;
//    }
//
//    @Override
//    public <T> Page<T> continueScroll(String s, long l, Class<T> aClass) {
//        return null;
//    }
//
//    @Override
//    public <T> Page<T> continueScroll(String s, long l, Class<T> aClass, SearchResultMapper searchResultMapper) {
//        return null;
//    }
//
//    @Override
//    public <T> void clearScroll(String s) {
//
//    }
//
//    @Override
//    public <T> Page<T> moreLikeThis(MoreLikeThisQuery moreLikeThisQuery, Class<T> aClass) {
//        return null;
//    }
//
//    @Override
//    public Boolean addAlias(AliasQuery aliasQuery) {
//        return null;
//    }
//
//    @Override
//    public Boolean removeAlias(AliasQuery aliasQuery) {
//        return null;
//    }
//
//    @Override
//    public List<AliasMetaData> queryForAlias(String s) {
//        return null;
//    }
//
//    @Override
//    public <T> T query(SearchQuery searchQuery, ResultsExtractor<T> resultsExtractor) {
//        return null;
//    }
//
//    @Override
//    public ElasticsearchPersistentEntity getPersistentEntityFor(Class clazz) {
//        Assert.isTrue(clazz.isAnnotationPresent(Document.class), "Unable to identify index name. " + clazz.getSimpleName()
//            + " is not a Document. Make sure the document class is annotated with @Document(indexName=\"foo\")");
//        return elasticsearchConverter.getMappingContext().getRequiredPersistentEntity(clazz);
//    }
//
//    public JestClient getJestClient() {
//        return null;
//    }
//
//    private <T> boolean createIndexIfNotCreated(Class<T> clazz) {
//        return indexExists(getPersistentEntityFor(clazz).getIndexName()) || createIndexWithSettings(clazz);
//    }
//
//    private <T> boolean createIndexWithSettings(Class<T> clazz) {
//        if (clazz.isAnnotationPresent(Setting.class)) {
//            String settingPath = clazz.getAnnotation(Setting.class).settingPath();
//            if (StringUtils.isNotBlank(settingPath)) {
//                String settings = readFileFromClasspath(settingPath);
//                if (StringUtils.isNotBlank(settings)) {
//                    return createIndex(getPersistentEntityFor(clazz).getIndexName(), settings);
//                }
//            } else {
//                logger.info("settingPath in @Setting has to be defined. Using default instead.");
//            }
//        }
//
//        return createIndex(getPersistentEntityFor(clazz).getIndexName(), getDefaultSettings(getPersistentEntityFor(clazz)));
//    }
//
//    private <T> Map getDefaultSettings(ElasticsearchPersistentEntity<T> persistentEntity) {
//
//        if (persistentEntity.isUseServerConfiguration()) {
//            return new HashMap();
//        }
//
//        return new MapBuilder<String, String>().put("index.number_of_shards", String.valueOf(persistentEntity.getShards()))
//                                               .put("index.number_of_replicas", String.valueOf(persistentEntity.getReplicas()))
//                                               .put("index.refresh_interval", persistentEntity.getRefreshInterval())
//                                               .put("index.store.type", persistentEntity.getIndexStoreType()).map();
//    }
//
//    private <T extends JestResult> T doExecute(Action<T> action) {
//        try {
//            return client.execute(action);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static String readFileFromClasspath(String url) {
//        StringBuilder stringBuilder = new StringBuilder();
//
//        BufferedReader bufferedReader = null;
//
//        try {
//            ClassPathResource classPathResource = new ClassPathResource(url);
//            InputStreamReader inputStreamReader = new InputStreamReader(classPathResource.getInputStream());
//            bufferedReader = new BufferedReader(inputStreamReader);
//            String line;
//
//            String lineSeparator = System.getProperty("line.separator");
//            while ((line = bufferedReader.readLine()) != null) {
//                stringBuilder.append(line).append(lineSeparator);
//            }
//        } catch (Exception e) {
//            logger.debug(String.format("Failed to load file from url: %s: %s", url, e.getMessage()));
//            return null;
//        } finally {
//            if (bufferedReader != null)
//                try {
//                    bufferedReader.close();
//                } catch (IOException e) {
//                    logger.debug(String.format("Unable to close buffered reader.. %s", e.getMessage()));
//                }
//        }
//
//        return stringBuilder.toString();
//    }
//
//    /** ============= Not Supported ============= */
//    @Override
//    public ElasticsearchConverter getElasticsearchConverter() {
//        throw new UnsupportedOperationException("Not supported yet. Just using simple converter");
//    }
//
//    @Override
//    public Client getClient() {
//        throw new UnsupportedOperationException("Not supported. Use getJestClient()");
//    }
//}
