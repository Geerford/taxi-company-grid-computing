package com.client;

import com.application.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class App {
    private InputTask task;

    public App(InputTask task){
        this.task = task;
    }

    public OutputTask perform(){
        List<Combination> combinations = new ArrayList<>();
        for(var combination : task.getCombinations()){
            int source = task.getTaxiWeight();
            Graph graph = task.getGraph();
            double resultWeight = 0;
            if(combination != null && combination.size() != 0){
                LinkedList<Integer> resultCombination = new LinkedList<>();
                LinkedList<Integer> resultPassengers = new LinkedList<>();
                while (combination.size() > 0){
                    int indexCombination = combination.remove();
                    Passenger passenger = task.getPassengers().get(indexCombination-1);
                    int destination = passenger.getPosition().getStart();
                    Dijkstra algorithm = new Dijkstra();
                    graph.clearGraph();
                    algorithm.calculate(graph.getVertex(source));
                    Vertex destinationVertex = graph.getVertex(destination);
                    for(var item : destinationVertex.getPath()){
                        resultCombination.addLast(item.getIndex());
                    }
                    resultWeight += destinationVertex.getMinDistance();
                    source = destination;
                    destination = passenger.getPosition().getEnd();
                    graph.clearGraph();
                    algorithm.calculate(graph.getVertex(source));
                    destinationVertex = graph.getVertex(destination);
                    for(var item : destinationVertex.getPath()){
                        resultCombination.addLast(item.getIndex());
                    }
                    resultCombination.addLast(destination);
                    source = destination;
                    resultWeight += destinationVertex.getMinDistance();
                    resultPassengers.addLast(passenger.getIndex());
                }
                combinations.add(new Combination(task.getTaxiIndex(), resultPassengers, resultCombination, resultWeight));
            }
        }
        return new OutputTask(combinations);
    }
}