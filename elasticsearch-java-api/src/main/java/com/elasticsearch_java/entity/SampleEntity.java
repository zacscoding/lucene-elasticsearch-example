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
    
    public SampleEntity(){}
    public SampleEntity(String id, String name, Integer age, Date regDate) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.regDate = regDate;
    }
    
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
    
    @Override
    public boolean equals(Object obj){
        if(obj == null)
            return false;
        if(!(obj instanceof SampleEntity)) {
            return false;
        }
        SampleEntity inst = (SampleEntity)obj;
        
        return age.equals(inst.age) && name.equals(inst.name) && regDate.equals(inst.regDate);
    }
    
    
}
