package com.lucene.learn.ch3_search;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import junit.framework.TestCase;

public class NearRealTimeTest extends TestCase {
    public void testNearRealTime() throws Exception {
        Directory dir = new RAMDirectory();
        IndexWriter writer = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_30), IndexWriter.MaxFieldLength.UNLIMITED);
        // save index
        for(int i=0; i<10; i++) {
            Document doc = new Document();
            doc.add(new Field("id", ""+i, Field.Store.NO, Field.Index.NOT_ANALYZED_NO_NORMS));
            doc.add(new Field("text", "aaa", Field.Store.NO, Field.Index.ANALYZED));
            writer.addDocument(doc);
        }
        
        // 준실시간 IndexReader
        // getReader() :: 현재 버퍼에 쌓여있는 모든 변경 사항을 Directory 안에 반영하고, 해당 변경 사항을
        // 포함하는 IndexReader 인스턴스를 생성
        IndexReader reader = writer.getReader();
        // 준실시간 IndexReader를 이용해 IndexSearcher 인스턴스 생성
        IndexSearcher searcher = new IndexSearcher(reader);
        
        Query query = new TermQuery(new Term("text","aaa"));
        TopDocs docs = searcher.search(query, 1);
        assertEquals(10,docs.totalHits);
        
        // 문서 1건 삭제
        writer.deleteDocuments(new Term("id","7"));
        
        // 문서 1건 추가
        Document doc = new Document();
        doc.add(new Field("id", "11", Field.Store.NO, Field.Index.NOT_ANALYZED_NO_NORMS));
        doc.add(new Field("text", "bbb", Field.Store.NO, Field.Index.ANALYZED));
        writer.addDocument(doc);
        
        // IndexReader 다시 열기
        // => 최근 인스턴스를 생성 한 이후로, 새로 생성된 색인 파일만 추가적으로 불러옴! good
        IndexReader newReader = reader.reopen();
        // check new
        assertFalse(reader == newReader);  
        reader.close();
                
        searcher = new IndexSearcher(newReader);
        
        TopDocs hits = searcher.search(query, 10);
        assertEquals(9,hits.totalHits);
        
        query = new TermQuery(new Term("text","bbb"));
        hits = searcher.search(query,1);
        assertEquals(1,hits.totalHits);
        
        newReader.close();
        writer.close();
    }

}
