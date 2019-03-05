package com.application;

import java.util.LinkedList;

public class Combination {
    private int taxiIndex;
    private LinkedList<Integer> path;
    private LinkedList<Integer> passengers;
    private double cost;

    public Combination(int taxiIndex, LinkedList<Integer> passengers, LinkedList<Integer> path, double cost){
        this.taxiIndex = taxiIndex;
        this.passengers = passengers;
        this.path = path;
        this.cost = cost;
    }

    public LinkedList<Integer> getPath() {
        return path;
    }

    public double getCost() {
        return cost;
    }

    public LinkedList<Integer> getPassengers() {
        return passengers;
    }

    public int getTaxiIndex() {
        return taxiIndex;
    }
}