package com.lucene.learn.ch3_search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import com.lucene.learn.util.TestUtil;

import junit.framework.TestCase;

public class QueryParserTest extends TestCase {    
    private Analyzer analyzer;
    private Directory dir;
    private IndexSearcher searcher;

    protected void setUp() throws Exception {
      analyzer = new WhitespaceAnalyzer();
      dir = TestUtil.getBookIndexDirectory();
      searcher = new IndexSearcher(dir);
    }

    protected void tearDown() throws Exception {
      searcher.close();
      dir.close();
    }
    
    public void testToString() throws Exception {
        BooleanQuery query = new BooleanQuery();
        query.add(new FuzzyQuery(new Term("field", "kountry")), BooleanClause.Occur.MUST);
        query.add(new TermQuery(new Term("title", "western")), BooleanClause.Occur.SHOULD);
        
        assertEquals("both kind", "+kountry~0.5 title:western", query.toString("field"));        
    }
    
    public void testTermQuery() throws Exception {
        QueryParser parser = new QueryParser(Version.LUCENE_30, "subject", analyzer);
        
        Query query = parser.parse("computers");
        // term : subject:computers 출력
        System.out.println("term : " + query);
    }
    
   // 텀 범위 검색
   public void testTermRangeQuery() throws Exception {
       // [Q TO V] : 양쪽 끝을 포함
       Query query = new QueryParser(Version.LUCENE_30, "subject", analyzer).parse("title2:[Q TO V]");      
       
       TopDocs matches = searcher.search(query, 10);
       assertTrue(TestUtil.hitsIncludeTitle(searcher, matches, "Tapestry in Action"));
       
       // { TO } : 양쪽 끝을 제외
       query = new QueryParser(Version.LUCENE_30, "subject", analyzer).parse("title2:{Q TO \"Tapestry in Action\"}");
       matches = searcher.search(query,10);
       
       assertFalse(TestUtil.hitsIncludeTitle(searcher, matches, "Tapestry in Action"));
   }
   
   // 접두어 , 와일드카드 질의
   public void testLowercasing() throws Exception {
       // PrefixQuery
       Query q = new QueryParser(Version.LUCENE_30, "field", analyzer).parse("PrefixQuery*");
       assertEquals("lowercased", "prefixquery*", q.toString("field"));
       
       QueryParser qp = new QueryParser(Version.LUCENE_30, "field", analyzer);
       qp.setLowercaseExpandedTerms(false);
       q = qp.parse("PrefixQuery*");
       
       assertEquals("not lowercased", "PrefixQuery*", q.toString("field"));       
   }
   
   // 구문 질의
   public void testPhraseQuery() throws Exception {
       // -> "This is Some Phrase*"  
       // -> StandardAnalyzer 로 분석  
       // -> this, is 불용어로 제거 & 큰 따옴표의 우선순위가 *보다 높음  
       // -> "some phrase" 구문 질의 생성
       Query query = new QueryParser(Version.LUCENE_30, "field", new StandardAnalyzer(Version.LUCENE_30))
                   .parse("\"This is Some Phrase\"");
       
       assertEquals("analyzed", "\"? ? some phrase\"", query.toString("field"));
       
       query = new QueryParser(Version.LUCENE_30, "field", analyzer).parse("\"term\"");
       assertTrue("reduced to TermQuery", query instanceof TermQuery);
   }
   
   public void testSlop() throws Exception {
       Query query = new QueryParser(Version.LUCENE_30, "field", analyzer).parse("\"exact phrase\"");
       assertEquals("zero slop", "\"exact phrase\"", query.toString("field"));
       
       QueryParser queryParser = new QueryParser(Version.LUCENE_30, "field", analyzer);
       queryParser.setPhraseSlop(5);
       query = queryParser.parse("\"sloppy phrase\"");
       assertEquals("sloppy, implicitly", "\"sloppy phrase\"~5", query.toString("field"));
   }
   
   // 퍼지 검색
   public void testFuzzyQuery() throws Exception {
       QueryParser parser = new QueryParser(Version.LUCENE_30, "subject", analyzer);
       Query query = parser.parse("kountry~");
       // fuzzy : subject:kountry~0.5       
       System.out.println("fuzzy : " + query.toString());
       
       query = parser.parse("kountry~0.7");
       // fuzzy2 : subject:kountry~0.7
       System.out.println("fuzzy2 : " + query.toString());
   }
   
   // 질의 그룹
   public void testGrouping() throws Exception {
       Query query = new QueryParser(Version.LUCENE_30, "subject", analyzer)
               .parse("(agile OR extreme) AND methodology");
       
       TopDocs matches = searcher.search(query,10);
       assertTrue(TestUtil.hitsIncludeTitle(searcher, matches, "Extreme Programming Explained"));
       assertTrue(TestUtil.hitsIncludeTitle(searcher, matches, "The Pragmatic Programmer"));
   }   
}
