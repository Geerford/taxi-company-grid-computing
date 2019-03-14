package com.application;

import java.util.List;

public class OutputTask {
    private List<Combination> combinations;

    public OutputTask(List<Combination> combinations){
        this.combinations = combinations;
    }

    public List<Combination> getCombinations() {
        return combinations;
    }
}