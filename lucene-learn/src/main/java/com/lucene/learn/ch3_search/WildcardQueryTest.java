package com.lucene.learn.ch3_search;

import java.io.IOException;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import junit.framework.TestCase;

public class WildcardQueryTest extends TestCase {
    private Directory directory;
    public void setUp() throws IOException {
        directory = new RAMDirectory();
    }    
    public void tearDown() throws IOException {
        if(directory != null) { try { directory.close(); }catch(IOException e){} }
    }
    private void indexSingleFieldDocs(Field[] fields) throws Exception {
        IndexWriter writer = new IndexWriter(directory, new WhitespaceAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
        
        for(Field f : fields) {
            Document doc = new Document();
            doc.add(f);
            writer.addDocument(doc);
        }
        
        writer.optimize();
        writer.close();
    }
    
    public void testWildcard() throws Exception {
        // given
        indexSingleFieldDocs(new Field[]{
                new Field("contents", "wild" , Field.Store.YES, Field.Index.ANALYZED),
                new Field("contents", "child" , Field.Store.YES, Field.Index.ANALYZED),
                new Field("contents", "mile" , Field.Store.YES, Field.Index.ANALYZED),
                new Field("contents", "mildew" , Field.Store.YES, Field.Index.ANALYZED)
        });
        
        // when
        IndexSearcher searcher = new IndexSearcher(directory);
        Query query = new WildcardQuery(new Term("contents","*ild*"));
        TopDocs matches = searcher.search(query, 10);
        
        // then
        assertEquals("mile no match", 3, matches.totalHits);
        assertEquals("score the same", matches.scoreDocs[0].score, matches.scoreDocs[1].score);
        assertEquals("score the same", matches.scoreDocs[1].score, matches.scoreDocs[2].score);
        
        searcher.close();
    }
    
    public void testFuzzy() throws Exception {
        // given
        indexSingleFieldDocs(new Field[] {
                new Field("contents", "fuzzy" , Field.Store.YES, Field.Index.ANALYZED),
                new Field("contents", "wuzzy" , Field.Store.YES, Field.Index.ANALYZED)
        });
        
        // when
        IndexSearcher searcher = new IndexSearcher(directory);
        Query query = new FuzzyQuery(new Term("contents", "wuzza"));
        TopDocs matches = searcher.search(query,10);
        
        // then
        assertEquals("both close enough", 2, matches.totalHits);
        assertTrue("wuzzy closer than fuzzy", matches.scoreDocs[0].score != matches.scoreDocs[1].score);
        Document doc = searcher.doc(matches.scoreDocs[0].doc);
        assertEquals("wuzza bear", "wuzzy", doc.get("contents"));        
        
        searcher.close();
    }
    
    public void testMatchAll() throws Exception {
        // given
        indexSingleFieldDocs(new Field[] {
                new Field("contents", "contents1" , Field.Store.YES, Field.Index.ANALYZED),
                new Field("contents", "contents2" , Field.Store.YES, Field.Index.ANALYZED),
                new Field("contents", "another1" , Field.Store.YES, Field.Index.ANALYZED),
                new Field("contents", "another2" , Field.Store.YES, Field.Index.ANALYZED)
        });
        
        // when
        IndexSearcher searcher = new IndexSearcher(directory);
        Query query = new MatchAllDocsQuery();
        TopDocs matches = searcher.search(query,10);
        
        assertEquals(matches.totalHits, 4);
        
        searcher.close();
    }
    

}


















