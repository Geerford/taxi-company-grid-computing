package com.application;

import java.util.LinkedList;
import java.util.List;

public class InputTask {
    private List<Passenger> passengers;
    private Graph graph;
    private List<LinkedList<Integer>> combinations;
    private int taxiIndex;
    private int taxiWeight;

    public InputTask(List<Passenger> Passengers, Graph graph, List<LinkedList<Integer>> Combinations, int TaxiIndex,
                     int taxiWeight){
        this.passengers = Passengers;
        this.graph = graph;
        this.combinations = Combinations;
        this.taxiIndex = TaxiIndex;
        this.taxiWeight = taxiWeight;
    }

    public Graph getGraph() {
        return graph;
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
}