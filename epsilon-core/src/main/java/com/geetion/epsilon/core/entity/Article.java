package com.geetion.epsilon.core.entity;

import java.io.Serializable;

public class Article implements Serializable {
    private long id;
    private double label;
    private String[] content;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLabel() {
        return label;
    }

    public void setLabel(double label) {
        this.label = label;
    }

    public String[] getContent() {
        return content;
    }

    public void setContent(String[] content) {
        this.content = content;
    }
}