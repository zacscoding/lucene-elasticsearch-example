package com.lucene.learn.ch3_search;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.lucene.learn.AbstractBookTest;
import com.lucene.learn.util.TestUtil;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-01-28
 * @GitHub : https://github.com/zacscoding
 */
public class TermQueryTest extends AbstractBookTest {
    @Test
    public void keyword() throws Exception {
        String field = "isbn";
        String value = "9781935182023";

        Term t = new Term(field, value);
        Query query = new TermQuery(t);

        TopDocs docs = searcher.search(query, 10);

        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            assertThat(value, is(doc.get(field)));
        }
    }
}