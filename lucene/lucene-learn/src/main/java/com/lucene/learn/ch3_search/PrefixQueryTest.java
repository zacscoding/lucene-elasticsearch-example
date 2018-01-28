package com.lucene.learn.ch3_search;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.lucene.learn.AbstractBookTest;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-01-29
 * @GitHub : https://github.com/zacscoding
 */
public class PrefixQueryTest extends AbstractBookTest {
    @Test
    public void prefixQuery() throws Exception {
        String field = "category";
        String value = "/technology/computers/programming";
        Term term = new Term(field, value);

        // 하위 계층을 포함하여 검색
        PrefixQuery prefixQuery = new PrefixQuery(term);
        TopDocs matches = searcher.search(prefixQuery, 10);
        System.out.println("## Total hits :: " + matches.totalHits);
        for (ScoreDoc scoreDoc : matches.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String categoryValue = doc.get("category");
            assertTrue(categoryValue.startsWith(value));
        }

        // 하위 계층을 제외하고 검색
        matches = searcher.search(new TermQuery(term), 10);
        System.out.println("## Total hits :: " + matches.totalHits);
        for (ScoreDoc scoreDoc : matches.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            String categoryValue = doc.get("category");
            assertThat(categoryValue, is(value));
        }
    }
}
