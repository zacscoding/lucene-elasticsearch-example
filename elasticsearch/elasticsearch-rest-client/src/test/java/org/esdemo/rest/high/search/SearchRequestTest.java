package org.esdemo.rest.high.search;

import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.esdemo.util.SimpleLogger;
import org.junit.Test;

/**
 * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.x/java-rest-high-search.html#java-rest-high-search-request-highlighting
 *
 * @author zacconding
 * @Date 2018-11-20
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
public class SearchRequestTest extends SearchAbstractTest {

    @Override
    boolean useBefore() {
        return false;
    }

    @Override
    boolean useAfter() {
        return false;
    }

    @Test
    public void testTermsQuery() {
        String[] names = {"hiva1", "hiva2", "hiva4", "hiva5"};
        // restricts the request to an index
        SearchRequest searchRequest = new SearchRequest(testPersonIndex);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termsQuery("name", names)) // terms query
                           .sort(new FieldSortBuilder("age").order(SortOrder.DESC)) // sort
                           .fetchSource(new String[] {"age", "name"}, new String[0])
                           .from(0) // from
                           .size(3) // size
                           .timeout(new TimeValue(60, TimeUnit.SECONDS)); // timeout
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            log.info("## Search result..");
            log.info("## > total hits : {}", searchResponse.getHits().totalHits);
            log.info("## > took : {}", searchResponse.getTook().duration());
            Iterator<SearchHit> searchHitIterator = searchResponse.getHits().iterator();
            while (searchHitIterator.hasNext()) {
                SearchHit hit = searchHitIterator.next();
                log.info("index : {} | id : {} | score : {}", hit.getIndex(), hit.getId(), hit.getScore());
                log.info(">>>> {}", hit.getSourceAsString());
            }
            // ++ spring data elasticsearch ++
            // DefaultResultMapper mapper = new DefaultResultMapper();
            //Page<SearchTestPerson> page = mapper.mapResults(searchResponse, SearchTestPerson.class, new PageRequest(0, 10));
            //log.info("## page size : {} | total elts : {}", page.getContent().size(), page.getTotalElements());
            // ++ spring data elasticsearch ++
        } catch (IOException e) {
            SimpleLogger.error("IOException occur", e);
        } catch (ElasticsearchStatusException e) {
            log.error("Failed to search.", e);
        }
    }

    @Test
    public void testHighlight() {
        /*
        {
            "query" : {
                "prefix" : {
                    "intro" : {"value" : "hiva"}
                }
            },
            "highlight" : {
                "fields" : {
                    "intro" : {
                        "pre_tags" : ["<div><strong>"],
                        "post_tags" : ["</strong>", "</div>"]
                    }
                }
            }
        }
         */
        SearchRequest searchRequest = new SearchRequest(testPersonIndex);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        HighlightBuilder highlightBuilder = new HighlightBuilder();

        HighlightBuilder.Field highlightIntro = new HighlightBuilder.Field("intro");
        highlightIntro.preTags("<strong>")
                      .postTags("</strong>");
        highlightBuilder.field(highlightIntro);

        searchSourceBuilder.query(
                                QueryBuilders.prefixQuery("intro", "hiva")
                            )
                           .size(5)
                           .highlighter(highlightBuilder);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SimpleLogger.build()
                        .appendln(">> search : {}", searchSourceBuilder.toString())
                        .appendln(">> total hits : {}", searchResponse.getHits().totalHits)
                        .flush();
            Iterator<SearchHit> searchHitIterator = searchResponse.getHits().iterator();
            while (searchHitIterator.hasNext()) {
                SearchHit hit = searchHitIterator.next();
                HighlightField field = hit.getHighlightFields().get("intro");
                if (field != null) {
                    SimpleLogger.println(">> Highlight : {}", field.getFragments()[0]);
                }
            }
        } catch(IOException e) {
            log.error("IOException occur", e);
        } catch(ElasticsearchStatusException e) {
            log.error("ElasticsearchStatusException occur", e);
        }
/*
Output
>> search : {"size":5,"query":{"prefix":{"intro":{"value":"hiva","boost":1.0}}},"highlight":{"fields":{"intro":{"pre_tags":["<strong>"],"post_tags":["</strong>"]}}}}
>> total hits : 10
>> Highlight : SearchTestPerson{age=2, name='<strong>hiva2</strong>', hobbies=[coding, game, movie, music, football]}
>> Highlight : SearchTestPerson{age=4, name='<strong>hiva4</strong>', hobbies=[coding, game, movie, music, football, baseball]}
>> Highlight : SearchTestPerson{age=5, name='<strong>hiva5</strong>', hobbies=[game, movie, music, football, baseball]}
>> Highlight : SearchTestPerson{age=7, name='<strong>hiva7</strong>', hobbies=[coding, music, football, baseball]}
>> Highlight : SearchTestPerson{age=6, name='<strong>hiva6</strong>', hobbies=[coding, movie, music, football, baseball]}
 */
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    Function<Integer, SearchTestPerson> createTestPersons() {
        return (i) -> {
            SearchTestPerson p = new SearchTestPerson();
            p.setAge(i);
            p.setName("hiva" + i);
            p.setHobbies(randomHobbies());
            p.setIntro(p.toString());
            return p;
        };
    }

    private String[] getHobbies() {
        return new String[] {"coding", "game", "movie", "music", "football", "baseball"};
    }

    private String[] randomHobbies() {
        Random random = new Random();

        String[] hobbies = getHobbies();
        String[] ret = new String[hobbies.length];
        int offset = 0;

        for (String hobby : hobbies) {
            if (random.nextInt(100) % 5 == 0) {
                continue;
            }
            ret[offset++] = hobby;
        }

        String[] retArr = new String[offset];
        System.arraycopy(ret, 0, retArr, 0, offset);
        return retArr;
    }

    @Override
    int documentSize() {
        return 10;
    }
}
