package com.lucene.learn.ch1_intro;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {
    /**
     * 어플리케이션을 실행하는 메인 메소드
     * 
     * @author zaccoding
     * @date 2017. 8. 19.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        //String indexDir = ScannerUtils.nextLine(scanner,"[루씬 색인을 저장할 디렉터리] : ");
        //String dataDir = ScannerUtils.nextLine(scanner, "[색인할 대상 파일이 들어있는 디렉터리] : ");
        String indexDir = GeneralConfig.INDEX_DIR;
        String dataDir = GeneralConfig.DATA_DIR;
        
        File dir = new File(indexDir);
        if(dir.exists())
            dir.delete();
        dir.mkdirs();
        
        long start = System.currentTimeMillis();
        Indexer indexer = new Indexer(indexDir);
        int numIndexed;
        
        try {
            numIndexed = indexer.index(dataDir, new TextFilesFilter());
        }
        finally {
            indexer.close();
        }
        
        long end = System.currentTimeMillis();
        System.out.println("Indexing " + numIndexed + "files took" + (end -start) + "ms");
                
        scanner.close();
    }
    
    private IndexWriter writer;
    /**
     * 생성자
     * @param indexDir 인덱싱 할 디렉터리
     * @throws IOException
     */
    public Indexer(String indexDir) throws IOException {
        Directory dir = FSDirectory.open(new File(indexDir));        
        writer = new IndexWriter(dir,new StandardAnalyzer(Version.LUCENE_30),true,IndexWriter.MaxFieldLength.UNLIMITED);        
    }
    
    public void close() throws IOException {
        writer.close();            
    }
    
    /**
     * data가 존재하는 디렉터리로 부터 인덱싱을 처리하는 메소드
     * 
     * @author zaccoding
     * @date 2017. 8. 19.
     * @param dataDir : 데이터가 존재하는 디렉터리
     * @param filter : 인덱싱을 할지 말지 필터
     * @return : 색인 된 문서 건수
     * @throws Exception
     */
    public int index(String dataDir, FileFilter filter) throws Exception {
        File[] files = new File(dataDir).listFiles();
        
        for(File f : files) {
            if((!f.isDirectory()) && (!f.isHidden()) && (f.canRead()) && (f.exists()) && (filter == null || filter.accept(f)) ) {
                indexFile(f);
            }            
        }
        
        // 색인 된 문서 건수 리턴
        return writer.numDocs();
    }
    
    /**
     * 텍스트 파일 필터 클래스
     * .txt로 끝나는 파일만 accept 한다.
     * 
     * @author zaccoding
     * @date 2017. 8. 19.
     */
    private static class TextFilesFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            // FileFilter를 사용해 색인 할 txt 파일만 추가
            return pathname.getName().toLowerCase().endsWith(".txt");
        }
    }
    
    protected Document getDocument(File f) throws Exception {
        Document doc = new Document();
        
        // 파일 내용 추가
        doc.add(new Field("contents", new FileReader(f)));
        // 파일 이름 추가
        doc.add(new Field("filename",f.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        
        return doc;
    }
    
    private void indexFile(File f) throws Exception {
        System.out.println("Indexing " + f.getCanonicalPath());
        Document doc = getDocument(f);
        writer.addDocument(doc);
    }
}

