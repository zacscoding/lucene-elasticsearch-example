package com.lucene.learn.ch3_search;

import static org.junit.Assert.assertTrue;

import com.lucene.learn.util.CustomPrinter;
import java.io.IOException;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-01-28
 * @GitHub : https://github.com/zacscoding
 */
public class NumericRangeQueryTest {

    private Directory dir;
    private String field;
    private int[] values;
    private IndexSearcher searcher;

    @Before
    public void setUp() throws Exception {
        dir = new RAMDirectory();
        field = "age";
        values = new int[] {
            10, 1, 22, 25, 29, 55, 45
        };
        IndexWriter writer = new IndexWriter(dir, new WhitespaceAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
        for (int value : values) {
            Document doc = new Document();
            doc.add(new NumericField(field, Store.YES, true).setIntValue(value));
            writer.addDocument(doc);
        }
        writer.close();
        searcher = new IndexSearcher(dir);
    }

    @After
    public void tearDown() throws Exception {
        searcher.close();
        dir.close();
    }

    @Test
    public void range() throws IOException {
        NumericRangeQuery<Integer> query = NumericRangeQuery.newIntRange(field, 29, 55, true, false);
        TopDocs match = searcher.search(query, 10);

        assertTrue(match.totalHits == 2);

        for(ScoreDoc scoreDoc : match.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            int age = Integer.parseInt(doc.get(field));
            assertTrue(age >= 29 && age < 55);
        }
    }
}
