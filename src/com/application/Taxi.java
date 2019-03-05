package com.application;

public class Taxi {
    private int index;
    private int weight;

    public Taxi(int weight){
        this.weight = weight;
    }

    public Taxi(int index, int weight){
        this.index = index;
        this.weight = weight;
    }

    public int getIndex() {
        return index;
    }

    public int getWeight() {
        return weight;
    }
}