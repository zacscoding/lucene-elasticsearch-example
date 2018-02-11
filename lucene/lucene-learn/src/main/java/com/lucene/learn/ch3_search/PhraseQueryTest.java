package com.lucene.learn.ch3_search;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.lucene.learn.AbstractRamDirTest;
import java.io.IOException;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-02-11
 * @GitHub : https://github.com/zacscoding
 */
public class PhraseQueryTest extends AbstractRamDirTest {

    @Override
    public void setUp() throws Exception {
        super.setUp();

        Document doc = new Document();
        doc.add(new Field("field", "the quick brown fox jumped over the lazy dog", Store.YES, Index.ANALYZED));
        indexWriter.addDocument(doc);
        indexWriter.close();

        indexSearcher = new IndexSearcher(dir);
    }

    @Test
    public void slopComparison() throws Exception {
        String[] phrase = new String[]{"quick", "fox"};
        assertFalse("extract phrase not found", matched(phrase, 0));
        assertTrue("close enough", matched(phrase, 1));


    }

    @Test
    public void reverse() throws Exception {
        String[] phrase = new String[]{"fox", "quick"};
        assertFalse("hop flop", matched(phrase, 2));
        assertTrue("hop hop slop", matched(phrase, 3));
    }

    @Test
    public void multiple() throws Exception {
        // "field" : "the quick brown fox jumped over the lazy dog"

        // quick -> -> , lazy <- <-
        assertFalse("not close enough", matched(new String[]{"quick", "jumped", "lazy"}, 3));
        assertTrue("just enough", matched(new String[]{"quick", "jumped", "lazy"}, 4));

        assertFalse("not close enough", matched(new String[]{"lazy", "jumped", "quick"}, 7));
        assertTrue("not close enough", matched(new String[]{"lazy", "jumped", "quick"}, 8));
    }


    private boolean matched(String[] phrase, int slop) throws IOException {
        PhraseQuery query = new PhraseQuery();
        query.setSlop(slop);

        for (String word : phrase) {
            query.add(new Term("field", word));
        }

        TopDocs matches = indexSearcher.search(query, 10);
        return matches.totalHits > 0;
    }
}
