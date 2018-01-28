package com.lucene.learn;

import com.lucene.learn.util.TestUtil;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.junit.After;
import org.junit.Before;

/**
 * @author zacconding
 * @Date 2018-01-29
 * @GitHub : https://github.com/zacscoding
 */
public abstract class AbstractBookTest {
    protected Directory dir;
    protected IndexSearcher searcher;

    @Before
    public void setUp() throws Exception {
        dir = TestUtil.getBookIndexDirectory();
        searcher = new IndexSearcher(dir);
    }

    @After
    public void tearDown() throws Exception {
        if (searcher != null) {
            searcher.close();
        }
        if (dir != null) {
            dir.close();
        }
    }
}