package com.lucene.learn.ch3_search;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.lucene.learn.AbstractRamDirTest;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-02-06
 * @GitHub : https://github.com/zacscoding
 */
public class BooleanQueryTest extends AbstractRamDirTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        for (int i = 1; i <= 10; i++) {
            Document doc = new Document();
            doc.add(new Field("name", "name" + i, Field.Store.YES, Index.NOT_ANALYZED_NO_NORMS));
            doc.add(new NumericField("age", Field.Store.YES, true).setIntValue(i));
            indexWriter.addDocument(doc);
        }
        indexWriter.close();
    }

    @Test
    public void boolQueryTest() throws Exception {
        indexSearcher = new IndexSearcher(dir);
        orTest();
        andTest();
    }

    private void orTest() throws Exception {
        BooleanQuery boolQuery = new BooleanQuery();
        for (int i = 1; i <= 3; i++) {
            boolQuery.add(new TermQuery(new Term("name", "name" + i)), BooleanClause.Occur.SHOULD);
        }
        TopDocs matches = indexSearcher.search(boolQuery, 10);
        assertTrue(matches.totalHits == 3);
        for (ScoreDoc scoreDoc : matches.scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            String name = doc.get("name");
            assertTrue((name.equals("name1") || name.equals("name2") || name.equals("name3")));
        }
    }

    private void andTest() throws Exception {
        // bool query
        TermQuery nameQuery = new TermQuery(new Term("name", "name2"));
        NumericRangeQuery<Integer> ageQuery = NumericRangeQuery.newIntRange("age", 2, 5, true, false);
        BooleanQuery boolQuery = new BooleanQuery();
        boolQuery.add(nameQuery, BooleanClause.Occur.MUST);
        boolQuery.add(ageQuery, BooleanClause.Occur.MUST);

        // search
        TopDocs matches = indexSearcher.search(boolQuery, 10);

        // then
        assertTrue(matches.totalHits == 1);
        Document doc = indexSearcher.doc(matches.scoreDocs[0].doc);
        assertThat("name2", is(doc.get("name")));
        int age = Integer.parseInt(doc.get("age"));
        assertTrue((age >= 2) && (age < 5));
    }
}
