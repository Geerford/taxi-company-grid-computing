package com.application;

public class EdgeSimple {
    private int source;
    private int destination;
    private int weight;

    public EdgeSimple(int start, int end, int weight){
        this.source = start;
        this.destination = end;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }

    public int getDestination() {
        return destination;
    }

    public int getSource() {
        return source;
    }
}