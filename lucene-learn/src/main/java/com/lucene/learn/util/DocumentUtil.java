package com.lucene.learn.util;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;

public class DocumentUtil {    
    public static Document getDocumentFromSearch(IndexSearcher searcher, TopDocs docs, int idx) throws CorruptIndexException, IOException {        
        return searcher.doc(docs.scoreDocs[idx].doc);
    }
    
    public static void displayDocument(String prefix, Document docu, String suffix) {
        System.out.println(prefix);
        displayDocument(docu);
        System.out.println(suffix);
    }
    
    public static void displayDocument(Document docu) {
        if(docu == null) {
            System.out.println("## [displayDocument] docu is null");
            return;
        }        
        docu.getFields().forEach(f->{
            System.out.println(f.name()+ " : " + docu.get(f.name()));            
        });
    }
}
