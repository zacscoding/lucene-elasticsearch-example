package com.estest.model.withdoc;

import com.estest.util.JacksonUtils;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "books",type = "book" , shards = 1, replicas = 0, refreshInterval = "-1")
public class SimpleBook {
    @Id
    private String id;
    private String title;
    private String author;
    private String date;
    private Integer pages;
    private String category;
    
   
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public Integer getPages() {
        return pages;
    }
    public void setPages(Integer pages) {
        this.pages = pages;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    
    @Override
    public String toString() {
        return JacksonUtils.writeValueAsString(this);
    }
}
