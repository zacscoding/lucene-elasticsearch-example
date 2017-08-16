package com.elasticsearch_java.entity;

import java.util.Date;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.gson.Gson;

@JsonNaming(SnakeCaseStrategy.class)
public class SampleEntity {
    private String id;
    private String name;
    private Integer age;
    private Date regDate;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public Date getRegDate() {
        return regDate;
    }
    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }
    
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }    
}
