package com.lucene.learn.ch3_search;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import com.lucene.learn.util.DocumentUtil;
import com.lucene.learn.util.TestUtil;

import junit.framework.TestCase;

public class QueryTest extends TestCase {
    Directory dir;
    IndexSearcher searcher;
    
    public void setUp() throws Exception {
        dir = TestUtil.getBookIndexDirectory();
        searcher = new IndexSearcher(dir); 
    }
    
    public void tearDown() throws Exception {
        if(searcher != null) {
            searcher.close();
        }
        if(dir != null) {
            dir.close();
        }
    }
    
    /**
     * Term Query Test
     */
    public void testKeyword() throws Exception {
        Term t = new Term("isbn","9781935182023");
        Query query = new TermQuery(t);
        TopDocs topDocs = searcher.search(query, 10);
        assertEquals("JUnit in Action, Second Edition", 1, topDocs.totalHits);
        
        searcher.close();
        dir.close();
    }
    
    /**
     * TermRangeQuery
     */
    public void testTermRangeQuery() throws Exception {
        // d* ~ j*
        TermRangeQuery query = new TermRangeQuery("title2", "d", "j", true, true); // boolean, boolean == contains
        TopDocs matches = searcher.search(query, 100);
        assertEquals(3,matches.totalHits);
        
        for(int i=0; i< matches.totalHits; i++) {
            Document doc = searcher.doc(matches.scoreDocs[i].doc);
            String title2 = doc.get("title2");
            assertNotNull(title2);
            char first = title2.charAt(0);
            assertTrue( first >= 'd' && first <= 'j' );            
        }
        
        searcher.close();
        dir.close();
    }
    
    /**
     * NumericRangeQuery : inclusive
     */
    public void testInclusive() throws Exception {
        // TTC의 출간일 == 2006년 9월
        NumericRangeQuery<Integer> query = NumericRangeQuery.newIntRange("pubmonth", 200605, 200609, true, true);
        TopDocs matches = searcher.search(query, 10);
        assertEquals(1, matches.totalHits);        
        Document doc = searcher.doc(matches.scoreDocs[0].doc);
        String pubMonth = doc.get("pubmonth");
        assertTrue(pubMonth.compareTo("200605") >= 0);
        assertTrue(pubMonth.compareTo("200609") <= 0);                
    }
    /**
     * NumericRangeQuery : exclusive 
     */
    public void testExclusive() throws Exception { 
        // TTC 출간일 == 2006년 9월
        NumericRangeQuery<Integer> query = NumericRangeQuery.newIntRange("pubmonth", 200605, 200609, false, false);
        TopDocs matches = searcher.search(query, 10);
        assertTrue(matches.totalHits == 0);
    }
    
    /**
     * PrefixQuery 
     */
    public void testPrefix() throws Exception {
        // 하위 계층을 포함 해 검색 == startsWith("/technology/computers/programming")
        Term term = new Term("category","/technology/computers/programming");
        PrefixQuery query = new PrefixQuery(term);
        
        TopDocs matches = searcher.search(query, 10);
        int programmingAndBelow = matches.totalHits;
        for(int i=0; i<programmingAndBelow; i++) {
            Document doc = DocumentUtil.getDocumentFromSearch(searcher, matches, i);
            assertTrue(doc.get("category").startsWith("/technology/computers/programming"));
        }
        
        // 하위 계층 제외하고 검색
        matches = searcher.search(new TermQuery(term), 10);        
        int justProgramming = matches.totalHits;
        assertTrue(programmingAndBelow > justProgramming);
        
        for(int i=0; i<justProgramming; i++) {
            Document doc = DocumentUtil.getDocumentFromSearch(searcher, matches, i);
            assertTrue(doc.get("category").equals("/technology/computers/programming"));
        }
    }
    
    /**
     * BooleanQuery : and
     */
    public void testAnd() throws Exception {
        TermQuery searchingBooks = new TermQuery(new Term("subject","search"));        
        Query books2010 = NumericRangeQuery.newIntRange("pubmonth", 201001, 201012, true, true);
        
        BooleanQuery searchingBooks2010 = new BooleanQuery();
        searchingBooks2010.add(searchingBooks, BooleanClause.Occur.MUST);
        searchingBooks2010.add(books2010, BooleanClause.Occur.MUST);
        
        TopDocs matches = searcher.search(searchingBooks2010, 10);        
        assertTrue(TestUtil.hitsIncludeTitle(searcher, matches, "Lucene in Action, Second Edition"));        
    }
    
    /**
     * BooleanQuery : or 
     */
    public void testOr() throws Exception {
        // 첫번째 카테고리
        TermQuery methodologyBooks = new TermQuery(new Term("category","/technology/computers/programming/methodology"));
        // 두번째 카테고리
        TermQuery easternPhilosophyBooks = new TermQuery(new Term("category","/philosophy/eastern"));
        
        // OR
        BooleanQuery enlightenmentBooks = new BooleanQuery();
        enlightenmentBooks.add(methodologyBooks, BooleanClause.Occur.SHOULD);
        enlightenmentBooks.add(easternPhilosophyBooks, BooleanClause.Occur.SHOULD);
        
        TopDocs matches = searcher.search(enlightenmentBooks, 10);
        System.out.println("or = " + enlightenmentBooks);
        
        assertTrue(TestUtil.hitsIncludeTitle(searcher, matches, "Extreme Programming Explained"));
        assertTrue(TestUtil.hitsIncludeTitle(searcher, matches, "Tao Te Ching \u9053\u5FB7\u7D93"));        
    }
}















