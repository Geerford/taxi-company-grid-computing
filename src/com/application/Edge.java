package com.application;

public class Edge {
    private final Vertex vertex;
    private final double weight;

    public Edge(Vertex vertex, double weight){
        this.vertex = vertex;
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public Vertex getVertex() {
        return vertex;
    }
}