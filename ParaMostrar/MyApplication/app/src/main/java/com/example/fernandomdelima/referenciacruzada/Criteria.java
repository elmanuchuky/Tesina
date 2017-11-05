package com.example.fernandomdelima.referenciacruzada;

import java.util.ArrayList;

/**
 * Created by Fernando M. de Lima on 10/23/2017.
 */

public class Criteria {
    private String key;
    private String description;
    private ArrayList<Option> optionList;

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

    public ArrayList<Option> getOptionList() {
        return optionList;
    }

    public void setOptionList(ArrayList<Option> optionList) {
        this.optionList = optionList;
    }

    public Criteria(){
    }

    @Override
    public String toString() {
        return "Criteria{" +
                "key='" + key + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
