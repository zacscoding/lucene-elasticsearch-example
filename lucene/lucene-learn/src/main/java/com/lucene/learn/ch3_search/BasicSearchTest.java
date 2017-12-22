package com.lucene.learn.ch3_search;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import com.lucene.learn.util.DocumentUtil;
import com.lucene.learn.util.TestUtil;

import junit.framework.TestCase;

public class BasicSearchTest extends TestCase {
    // TermQuery 
    public void testTerm() throws Exception {
        Directory dir = TestUtil.getBookIndexDirectory();
        IndexSearcher searcher = new IndexSearcher(dir);
        
        Term t = new Term("subject", "ant");
        Query query = new TermQuery(t);
        // ## [query] : subject:ant
        System.out.println("## [query] : " + query.toString());
        TopDocs docs = searcher.search(query, 10);
        // 'ant' 검색어에 대한 결과가 있는지 확인
        assertEquals("Ant in Action",1,docs.totalHits);
        
        t = new Term("subject", "junit");
        docs = searcher.search(new TermQuery(t),10);
        // junit 검색에 대한 결과가 2건인지 확인
        assertEquals("Ant in Action," + "JUnit in Action, SecondEdition", 2, docs.totalHits);
        
       searcher.close();       
       dir.close();        
    }
    
    // QueryParser 테스트
    public void testQueryParser() throws Exception {
        Directory dir = TestUtil.getBookIndexDirectory();
        IndexSearcher searcher = new IndexSearcher(dir);
        
        // SimpleAnalyzer ( 대문자 -> 소문자 )
        QueryParser parser = new QueryParser(Version.LUCENE_30, "contents", new SimpleAnalyzer());
        
        /** query test1 */
        Query query = parser.parse("+JUNIT +ANT -MOCK");
        // ## [query] : +contents:junit +contents:ant -contents:mock
        System.out.println("## [query] : " + query.toString());
        TopDocs docs = searcher.search(query, 10);
        assertEquals(1,docs.totalHits);
        
        Document document = searcher.doc(docs.scoreDocs[0].doc);
        assertEquals("Ant in Action", document.get("title"));
        DocumentUtil.displayDocument("query from +JUNIT +ANT -MOCK", document, "----------------------");
        
        /** query test2 */
        query = parser.parse("mock OR junit");
        docs = searcher.search(query, 10);
        assertEquals("Ant in Action, JUnit in Action, Second Edition", 2, docs.totalHits);
        // ## [query] : contents:mock contents:junit
        System.out.println("## [query] : " + query.toString());
        DocumentUtil.displayDocument("query from mock OR junit", searcher.doc(docs.scoreDocs[0].doc), "");
        DocumentUtil.displayDocument("", searcher.doc(docs.scoreDocs[1].doc), "----------------------");
                
        searcher.close();
        dir.close();
    }
}
