package com.geetion.epsilon.core.entity;

public class EpsilonModel {
    private String id;
    private byte[] model;

    public EpsilonModel(String id, byte[] model) {
        this.id = id;
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getModel() {
        return model;
    }

    public void setModel(byte[] model) {
        this.model = model;
    }
}