package com.lucene.learn;

import java.io.IOException;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.After;
import org.junit.Before;

/**
 * @author zacconding
 * @Date 2018-01-29
 * @GitHub : https://github.com/zacscoding
 */
public abstract class AbstractRamDirTest {
    protected Directory dir;
    protected IndexWriter indexWriter;
    protected IndexSearcher indexSearcher;

    @Before
    public void setUp() throws Exception {
        System.out.println("AbstractRamDirTest::setUp() is called");
        dir = new RAMDirectory();
        indexWriter = getWriter();
    }

    @After
    public void tearDown() {
        System.out.println("AbstractRamDirTest::tearDown() is called");
        try {
            if(indexWriter != null) {
                indexWriter.close();
            }
        }
        catch(Exception e) {

        }
        try {
            if(indexSearcher != null) {
                indexSearcher.close();
            }
        }
        catch(Exception e) {

        }
        try {
            if(dir != null) {
                dir.close();
            }
        }
        catch(Exception e) {

        }
    }

    protected IndexWriter getWriter() throws IOException {
        System.out.println("AbstractRamDirTest::getWriter()");
        return new IndexWriter(dir, new WhitespaceAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
    }
}
