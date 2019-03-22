package com.application;

import java.util.ArrayList;
import java.util.List;

public class Collector {
    private List<Combination> combinations = new ArrayList<>();

    public void addTask(List<Combination> task){
        combinations.addAll(task);
    }

    public List<Combination> getCombinations(){
        return combinations;
    }
}