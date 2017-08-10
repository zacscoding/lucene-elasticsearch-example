package com.estest.model;

import com.estest.util.JacksonUtils;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Date;

/**
 * Logs entity
 * 
 * index : yyyy-mm-dd
 * type : logs
 * 
 * https://fasterxml.github.io/jackson-core/javadoc/2.8/
 * https://fasterxml.github.io/jackson-databind/javadoc/2.8/	
 * @author mskim
 * @date 2017. 8. 10.
 *
 */
@JsonNaming(SnakeCaseStrategy.class)
public class Logs {
	private String logContent;
	private Date time;
	
	public String getLogContent() {
		return logContent;
	}
	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	
	@Override
	public String toString() {
		return JacksonUtils.writeValueAsString(this);
	}
}
