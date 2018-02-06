package com.lucene.learn.ch2_index;

import com.lucene.learn.domain.Person;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-01-28
 * @GitHub : https://github.com/zacscoding
 */
public class indexingTest2 {

    private List<Person> persons;
    private Directory directory;

    @Before
    public void setUp() throws Exception {
        directory = new RAMDirectory();
        IndexWriter writer = getWriter();
        persons = new ArrayList<>();

        IntStream.range(1, 11).forEach(i -> {
            Person p = new Person();
            p.setName("name" + i);
            p.setAge(i * 10);
            persons.add(p);
        });

        for (int i = persons.size() - 1; i >= 0; i--) {
            Person p = persons.get(i);
            Document doc = new Document();
            doc.add(new Field("name", p.getName(), Field.Store.YES, Index.NOT_ANALYZED_NO_NORMS));
            doc.add(new NumericField("age", Field.Store.YES, true).setIntValue(p.getAge()));
            writer.addDocument(doc);
        }
        writer.close();
    }

    @Test
    public void searchBySort() throws Exception {
        IndexSearcher searcher = new IndexSearcher(directory);

        Query query = new MatchAllDocsQuery();
        Sort sort = Sort.INDEXORDER;
        sort.setSort(new SortField("name", SortField.STRING));
        TopDocs docs = searcher.search(query, null, 10, sort);
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println(doc.get("name"));
        }
        searcher.close();
        directory.close();
    }

    private IndexWriter getWriter() throws IOException {
        return new IndexWriter(directory, new WhitespaceAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
    }
}