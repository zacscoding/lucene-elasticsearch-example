package com.lucene.learn.ch3_search;

import com.lucene.learn.util.CustomPrinter;
import com.lucene.learn.util.TestUtil;
import java.io.File;

import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Explainer {

    private static String INDEX_DIR = TestUtil.INDEX_DIR;
    private static String QUERY_EXPRESSION = "junit";

    public static void main(String[] args) throws Exception {
        if (args != null && args.length == 2) {
            if (args[0] != null) {
                INDEX_DIR = args[0];
            }
        }

        Directory directory = FSDirectory.open(new File(INDEX_DIR));
        QueryParser queryParser = new QueryParser(Version.LUCENE_30, "contents", new SimpleAnalyzer());
        Query query = queryParser.parse(QUERY_EXPRESSION);
        System.out.println("Query : " + QUERY_EXPRESSION);

        IndexSearcher searcher = new IndexSearcher(directory);
        TopDocs topDocs = searcher.search(query, 10);
        for (ScoreDoc match : topDocs.scoreDocs) {
            Explanation explanation = searcher.explain(query, match.doc);
            System.out.println("---------------------------------");
            Document doc = searcher.doc(match.doc);
            // System.out.println("title : " + doc.get("title"));
            // System.out.println(explanation.toString());
            // System.out.println(explanation.toHtml());
            System.out.println(CustomPrinter.toJsonWithPretty(displayDocs(doc)));
            // System.out.println(CustomPrinter.toJsonWithPretty(doc));
            System.out.println(CustomPrinter.toJsonWithPretty(explanation));
        }

        searcher.close();
        directory.close();
    }

    private static Map<String,Object> displayDocs(Document doc) {
        Map<String,Object> ret = new HashMap<>();
        for(Fieldable field : doc.getFields()) {
            String name = field.name();
            ret.put(name,doc.get(name));
        }
        return ret;
    }
}
