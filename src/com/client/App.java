package com.client;

import com.application.*;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class App {
    private InputTask task;
    private Graph graph;

    public App(String task){
        this.task = new Gson().fromJson(task, InputTask.class);
        try{
            graph = initGraph(this.task.getCountVertex(), this.task.getEdges());
        }
        catch (NullPointerException ex){
            System.out.println("The graph is incorrectly initialized.");
        }
    }

    public OutputTask perform(){
        List<Combination> combinations = new ArrayList<>();
        for(var combination : task.getCombinations()){
            int source = task.getTaxiWeight();
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

    private static Graph initGraph(int numberVertices, LinkedList<EdgeSimple> edges){
        Graph graph = new Graph(numberVertices);
        for(EdgeSimple edge : edges){
            if(edge.getSource() > numberVertices || edge.getDestination() > numberVertices){
                throw new NullPointerException();
            }
            graph.addEdge(edge);
        }
        return graph;
    }
}