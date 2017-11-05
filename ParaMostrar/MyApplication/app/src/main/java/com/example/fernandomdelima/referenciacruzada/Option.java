package com.example.fernandomdelima.referenciacruzada;

import java.util.ArrayList;

/**
 * Created by Fernando M. de Lima on 10/23/2017.
 */

public class Option {
    private String key;
    private String description;
    private ArrayList<String> entities;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<String> entities) {
        this.entities = entities;
    }

    public Option(){
    }

    @Override
    public String toString() {
        return "Option{" +
                "key='" + key + '\'' +
                ", description='" + description + '\'' +
                ", entities=" + entities +
                '}';
    }
}
