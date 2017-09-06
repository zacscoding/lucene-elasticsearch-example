package com.lucene.learn.util;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class TestUtil {
    public static int hitCount(IndexSearcher searcher, Query query) throws IOException{
        return searcher.search(query, 1).totalHits;
    }

    public static Directory getBookIndexDirectory() throws IOException{
        // The build.xml ant script sets this property for us:
        final String indexDir = "D:\\lucene\\resource\\lia2e\\build\\index";
        return FSDirectory.open(new File(indexDir));
    }
    
    public static boolean hitsIncludeTitle(IndexSearcher searcher, TopDocs matches, String ... titles) throws CorruptIndexException, IOException {
        if(titles == null || titles.length == 0) {
            return true;            
        }
        
        Set<String> titleSet = new HashSet<>((int)(titles.length*1.5), 0.999999f);
        for(String title : titles) {
            titleSet.add(title);
        }
        
        for(int i=0; i<matches.totalHits; i++) {            
            Document doc = searcher.doc(matches.scoreDocs[i].doc);
            if(titleSet.contains( doc.get("title") )) {                
                return true;
            }            
        }
        
        return false;
    }

}
