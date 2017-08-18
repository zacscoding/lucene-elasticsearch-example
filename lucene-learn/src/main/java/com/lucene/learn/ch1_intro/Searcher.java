package com.lucene.learn.ch1_intro;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {
    public static void main(String[] args) throws IOException, ParseException {
        String indexDir = GeneralConfig.INDEX_DIR;
        //String q = ScannerUtils.nextLine("[search query] : ");
        String q = "patent";
        
        search(indexDir,q);
    }
    
    public static void search(String indexDir, String q) throws IOException, ParseException {
        // 색인 열기
        Directory dir = FSDirectory.open(new File(indexDir));        
        IndexSearcher is = new IndexSearcher(dir);
        
        // 질의 분석
        QueryParser parser = new QueryParser(Version.LUCENE_30,"contents",new StandardAnalyzer(Version.LUCENE_30));
        Query query = parser.parse(q);

        long start = System.currentTimeMillis();
        // 색인 내용 검색
        TopDocs hits = is.search(query, 10); // 실제 결과 문서에 대한 참조만 존재
        long end = System.currentTimeMillis();
        
        // 검색 결과 출력
        System.err.println("Found " + hits.totalHits + " document(s) (in" + (end-start) + "ms) that matched query '" + q + "': " );
        
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc);
//            doc.getFields().forEach(f -> {
//                System.out.println("[filed : " + f.name() + ", value : " + doc.get(f.name()));
//            });
            System.out.println(doc.get("filename"));
        }
        
        is.close();
    }
    
    
}
