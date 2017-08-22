package com.lucene.learn.util;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
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

}
