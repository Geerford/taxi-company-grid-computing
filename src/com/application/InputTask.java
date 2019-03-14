package com.application;

import java.util.LinkedList;
import java.util.List;

public class InputTask {
    private List<Passenger> passengers;
    private List<LinkedList<Integer>> combinations;
    private int taxiIndex;
    private int taxiWeight;
    private int countVertex;
    private LinkedList<EdgeSimple> edges;

    public InputTask(List<Passenger> passengers, List<LinkedList<Integer>> combinations, int taxiIndex,
                     int taxiWeight, int countVertex, LinkedList<EdgeSimple> edges){
        this.passengers = passengers;
        this.combinations = combinations;
        this.taxiIndex = taxiIndex;
        this.taxiWeight = taxiWeight;
        this.countVertex = countVertex;
        this.edges = edges;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public List<LinkedList<Integer>> getCombinations() {
        return combinations;
    }

    public int getTaxiIndex() {
        return taxiIndex;
    }

    public int getTaxiWeight() {
        return taxiWeight;
    }

    public int getCountVertex() {
        return countVertex;
    }

    public LinkedList<EdgeSimple> getEdges() {
        return edges;
    }
}
