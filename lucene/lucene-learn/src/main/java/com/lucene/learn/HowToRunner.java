package com.lucene.learn;

import java.io.IOException;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-01-29
 * @GitHub : https://github.com/zacscoding
 */
public class HowToRunner extends AbstractRamDirTest {

    @Test
    public void test() {
        System.out.println("test!!!");
    }


    protected IndexWriter getWriter() throws IOException {
        System.out.println("HowToRunner::getWriter()");
        return new IndexWriter(dir, new WhitespaceAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
    }
}