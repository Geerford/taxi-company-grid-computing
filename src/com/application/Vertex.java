package com.application;

import java.util.ArrayList;
import java.util.LinkedList;

public class Vertex implements Comparable<Vertex>{
    private final int index;
    private ArrayList<Edge> neighbours;
    private LinkedList<Vertex> path;
    private double minDistance = Double.POSITIVE_INFINITY;

    public Vertex(int index){
        this.index = index;
        neighbours = new ArrayList<>();
        path = new LinkedList<>();
    }

    public int compareTo(Vertex other){
        return Double.compare(minDistance, other.minDistance);
    }

    public int getIndex(){
        return index;
    }

    public ArrayList<Edge> getNeighbours(){
        return neighbours;
    }

    public LinkedList<Vertex> getPath() {
        return path;
    }

    public void setPath(LinkedList<Vertex> path) {
        this.path = path;
    }

    public double getMinDistance(){
        return minDistance;
    }

    public void setMinDistance(double minDistance){
        this.minDistance = minDistance;
    }
}