package com.estest.model.nondoc.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Elastic Search의 Hit response entity
 * 
 * @author zaccoding
 * @date 2017. 8. 12.
 * @github : https://github.com/zacscoding
 * @param <T>
 */
public class Hit<T> {
    @JsonProperty(value = "_index")
    private String index;

    @JsonProperty(value = "_type")
    private String type;

    @JsonProperty(value = "_id")
    private String id;

    @JsonProperty(value = "_source")
    private T source;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public T getSource() {
        return source;
    }

    public void setSource(T source) {
        this.source = source;
    }
}
