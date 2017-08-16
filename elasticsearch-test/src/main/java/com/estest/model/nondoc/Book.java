package com.estest.model.nondoc;

import java.util.Date;

public class Book {
	private String id;
	private String title;
	private String author;
	private String category;
	private Date written;
	private Integer pages;
	private Integer sell;
	private String plot;
	
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Date getWritten() {
		return written;
	}
	public void setWritten(Date written) {
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
		return "Book [title=" + title + ", author=" + author + ", category=" + category + ", written=" + written
				+ ", pages=" + pages + ", sell=" + sell + ", plot=" + plot + "]";
	}
}
