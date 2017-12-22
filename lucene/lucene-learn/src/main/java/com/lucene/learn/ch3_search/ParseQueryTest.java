package com.lucene.learn.ch3_search;

import java.io.IOException;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import junit.framework.TestCase;

public class ParseQueryTest extends TestCase {
    private Directory dir;
    private IndexSearcher searcher;
    
    public void setUp() throws IOException {
        dir = new RAMDirectory();
        // WhitespaceAnalyzer == 공백 토큰 분리 // 기타 토큰 정규화 X // 대소문자 구별 X
        IndexWriter writer = new IndexWriter(dir, new WhitespaceAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
        Document doc = new Document();
        doc.add(new Field("field", "the quick brown fox jumped over the lazy dog", Field.Store.YES, Field.Index.ANALYZED));
        writer.addDocument(doc);
        writer.close();
        
        searcher = new IndexSearcher(dir);
    }
    
    public void tearDown() throws IOException {
        if(searcher != null) { try {searcher.close();}catch(IOException e){} }
        if(dir != null) { try { dir.close(); }catch(IOException e){} }
    }
    
    /**
     * PhraseQuery 
     */
    public void testSlopComparison() throws Exception {
        String[] phrase = new String[] {"quick", "fox"};
        
        assertFalse("exact phrase not found", matched(phrase, 0));
        // anything move ->
        assertTrue("close enough", matched(phrase, 1));        
    }
    
    /**
     * PhraseQuery - reverse phrase 
     */
    public void testReverse() throws Exception {        
        String[] phrase = new String[] {"fox", "quick"};
        
        assertFalse("hop flop", matched(phrase,2));
        // (fox -> -> && anything ->) == (slop == 3)
        assertTrue("hop hop slop", matched(phrase, 3));
    }
    
    private boolean matched(String[] phrase, int slop) throws IOException {
        // PhraseQuery 인스턴스 생성
        PhraseQuery query = new PhraseQuery();
        query.setSlop(slop); // default 0 == 연속 된 단어만 검색
        
        // 지정된 단어를 순서대로 구문으로 추가
        for(String word : phrase) {
            query.add(new Term("field",word));
        }
        
        TopDocs matches = searcher.search(query, 10);
        return matches.totalHits > 0; 
    }
    
    /**
     * 다중 텀 구문 
     */
    public void testMultiple() throws Exception {
        // "field" : "the quick brown fox jumped over the lazy dog"
        
        // quick -> -> , lazy <- <-
        assertFalse("not close enough", matched(new String[]{"quick", "jumped", "lazy"}, 3));        
        assertTrue("just enough", matched(new String[]{"quick", "jumped", "lazy"}, 4));
        
        // 
        assertFalse("almost but not quite", matched(new String[]{"lazy", "jumped", "quick"}, 7));
        assertTrue("bingo", matched(new String[]{"lazy", "jumped", "quick"}, 8));
    }
}















