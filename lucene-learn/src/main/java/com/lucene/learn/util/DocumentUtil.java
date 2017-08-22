package com.lucene.learn.util;

import org.apache.lucene.document.Document;

public class DocumentUtil {
    
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
