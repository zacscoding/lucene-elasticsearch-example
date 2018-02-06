package com.lucene.learn.ch2_index;

import com.lucene.learn.AbstractRamDirTest;
import com.lucene.learn.domain.Email;
import com.lucene.learn.util.CustomPrinter;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-02-06
 * @GitHub : https://github.com/zacscoding
 */
public class BoostTest extends AbstractRamDirTest {

    List<Email> emails;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        emails = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            emails.add(new Email("zac" + i + "@company.com", "other" + i, "other subject" + i, "other body" + i, "test"));
        }
        for (int i = 1; i <= 5; i++) {
            emails.add(new Email("zac" + i + "@zaccoding.com", "name" + i, "subject" + i, "body" + i, "test"));
        }
    }

    @Test
    public void boost() throws Exception {
        // save
        for (Email email : emails) {
            Document doc = new Document();
            doc.add(new Field("senderEmail", email.getSenderEmail(), Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field("senderName", email.getSenderName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field("subject", email.getSubject(), Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field("type", email.getType(), Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field("body", email.getBody(), Field.Store.NO, Field.Index.NOT_ANALYZED));
            if (isImportant(email.getSenderEmail().toLowerCase())) {
                doc.setBoost(1.5F);
            } else {
                doc.setBoost(0.1F);
            }
            indexWriter.addDocument(doc);
        }
        indexWriter.close();

        indexSearcher = new IndexSearcher(dir);
        // search
        // Query query = new MatchAllDocsQuery();
        Query query = new TermQuery(new Term("type","test"));
        TopDocs matches = indexSearcher.search(query, 15);
        System.out.println(matches.totalHits);
        int i = 1;
        for (ScoreDoc scoreDoc : matches.scoreDocs) {
            Document match = indexSearcher.doc(scoreDoc.doc);
            CustomPrinter.println("## {}({}) :: senderEmail : {}, senderName : {}, subject : {}", (i++), match.getBoost(), match.get("senderEmail"), match.get("senderName"), match.get("subject"));
            Explanation explanation = indexSearcher.explain(query, scoreDoc.doc);
            System.out.println(explanation.toString());
            System.out.println("----------------------------");
        }
    }

    private boolean isImportant(String emailAddr) {
        return emailAddr.contains("@zaccoding.com");
    }
}