package com.lucene.learn.ch2_index;

import com.lucene.learn.util.TestUtil;
import junit.framework.TestCase;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author zaccoding
 * github : https://github.com/zacscoding
 * @Date : 2018-01-17
 */
public class IndexingTest2 {
    protected String[] ids =  {"1", "2"};
    protected String[] unindexed = {" Netherlands", "Italy" };
    protected String[] unstored = {"Amsterdam has lots of bridges", "Venice has lots of canals"};
    protected String[] text = {"Amsterdam", "Venice"};
    private Directory directory;

    @Before
    public void setUp() throws Exception {
        directory = new RAMDirectory();
        IndexWriter writer = getWriter();

        for(int i=0; i<ids.length; i++) {
            Document doc = new Document();
            doc.add(new Field("id", ids[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field("country", unindexed[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field("contents", unstored[i], Field.Store.NO, Field.Index.ANALYZED));
            doc.add(new Field("city", text[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
            writer.addDocument(doc);
        }

        writer.close();
    }

    /**
     * ================================================
     * 색인에 문서 추가
     * ================================================
     */
    @Test
    public void testIndexWriter() throws IOException {
        IndexWriter writer = getWriter();
        assertEquals(ids.length, writer.numDocs());
        writer.close();
    }

    @Test
    public void testIndexReader() throws IOException {
        IndexReader reader = IndexReader.open(directory);
        assertEquals(ids.length, reader.maxDoc());
        assertEquals(ids.length, reader.numDocs());

        reader.close();
    }

    /**
     * ================================================
     * 색인에서 문서 삭제
     * ================================================
     */

    @Test
    public void testDeleteBeforeOptimize() throws IOException {
        IndexWriter writer = getWriter();
        assertTrue(2 == writer.numDocs());

        // 첫번째 문서 삭제
        writer.deleteDocuments(new Term("id", "1"));
        writer.commit();
        // 색인에 삭제 된 문서가 있는지 검증
        assertTrue(writer.hasDeletions());
        assertEquals(2, writer.maxDoc());
        assertEquals(1, writer.numDocs());
        writer.close();
    }

    @Test
    public void testDeleteAfterOptimize() throws IOException {
        IndexWriter writer = getWriter();
        assertTrue(2 ==  writer.numDocs());
        writer.deleteDocuments(new Term("id","1"));
        writer.optimize();
        writer.commit();

        // 색인된 문서 1건, 삭제된 문서는 없음
        assertFalse(writer.hasDeletions());
        assertTrue(1 == writer.maxDoc());
        assertTrue(1 == writer.numDocs());
        writer.close();
    }

    /**
     * ================================================
     * 색인 된 문서 변경
     * ================================================
     */
    @Test
    public void testUpdate() throws IOException {
        assertTrue(1 == getHitCount("city", "Amsterdam"));

        IndexWriter writer = getWriter();

        Document doc = new Document();
        doc.add(new Field("id", "1", Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("country", "Netherlands", Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("contents", "Den Haag has a lot of museums", Field.Store.NO, Field.Index.ANALYZED));
        doc.add(new Field("city", "Den Haag", Field.Store.YES, Field.Index.NOT_ANALYZED));
        writer.updateDocument(new Term("id", "1"), doc);
        writer.close();

        assertTrue(0 == getHitCount("city", "Amsterdam"));
        assertTrue(1 == getHitCount("city", "Den Haag"));
    }




    private int getHitCount(String fieldName, String searchString) throws IOException {
        IndexSearcher searcher = new IndexSearcher(directory);
        Term t = new Term(fieldName, searchString);

        Query query = new TermQuery(t);
        int hitCouint = TestUtil.hitCount(searcher, query);
        searcher.close();

        return hitCouint;
    }

    private IndexWriter getWriter() throws IOException {
        return new IndexWriter(directory, new WhitespaceAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
    }
}
