package com.lucene.learn.ch3_search;

import static org.junit.Assert.assertTrue;

import com.lucene.learn.AbstractRamDirTest;
import java.io.IOException;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-01-28
 * @GitHub : https://github.com/zacscoding
 */
public class TermRangeQueryTest extends AbstractRamDirTest {

    private String field;
    private String[] values;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        field = "title";
        values = new String[]{
            "aaa", "bbb", "ccc", "ddd", "dee", "eee", "fff"
        };

        for (String value : values) {
            Document doc = new Document();
            doc.add(new Field(field, value, Field.Store.YES, Index.NOT_ANALYZED_NO_NORMS));
            indexWriter.addDocument(doc);
        }
        indexWriter.close();

        indexSearcher = new IndexSearcher(dir);
    }

    @Test
    public void range() throws IOException {
        TermRangeQuery query = new TermRangeQuery(field, "c", "e", true, false);
        TopDocs match = indexSearcher.search(query, 10);
        assertTrue(match.totalHits == 3);

        for (ScoreDoc scoreDoc : match.scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            String value = doc.get(field);
            assertTrue("c".compareTo(value) <= 0);
            assertTrue("e".compareTo(value) > 0);
        }
    }
}