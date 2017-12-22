package com.elasticsearch_java.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(SnakeCaseStrategy.class)
public class Book {
    private String id;
    private String author;
    private String category;
    private String written;
    private Integer pages;
    private Integer sell;
    private String plot;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getWritten() {
        return written;
    }
    public void setWritten(String written) {
        this.written = written;
    }
    public Integer getPages() {
        return pages;
    }
    public void setPages(Integer pages) {
        this.pages = pages;
    }
    public Integer getSell() {
        return sell;
    }
    public void setSell(Integer sell) {
        this.sell = sell;
    }
    public String getPlot() {
        return plot;
    }
    public void setPlot(String plot) {
        this.plot = plot;
    }
    @Override
    public String toString() {
        return "Book [id=" + id + ", author=" + author + ", category=" + category + ", written="
                + written + ", pages=" + pages + ", sell=" + sell + ", plot=" + plot + "]";
    }    
}
