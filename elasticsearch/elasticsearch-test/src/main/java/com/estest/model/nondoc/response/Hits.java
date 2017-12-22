package com.estest.model.nondoc.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public class Hits<T> {
    private List<Hit<T>> hits;
    private Long total;
    private Double maxScore;
    
    public List<Hit<T>> getHits() {
        return hits;
    }
    public void setHits(List<Hit<T>> hits) {
        this.hits = hits;
    }
    public Long getTotal() {
        return total;
    }
    public void setTotal(Long total) {
        this.total = total;
    }
    public Double getMaxScore() {
        return maxScore;
    }
    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }
}
