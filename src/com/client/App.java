package com.client;

import com.application.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class App {
    public static void main(String args[]){
        String fileNameInput = args[0];
        String fileNameOutput = args[1];
        InputTask task;
        Graph graph;
        try {
            JsonReader reader = new JsonReader(new FileReader(fileNameInput));
            task = new Gson().fromJson(reader, InputTask.class);
            try{
                graph = initGraph(task.getCountVertex(), task.getEdges());
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
                        combinations.add(new Combination(task.getTaxiIndex(), resultPassengers, resultCombination,
                                resultWeight));
                    }
                }
                try (Writer writer = new FileWriter(fileNameOutput)) {
                    Gson gson = new GsonBuilder().create();
                    gson.toJson(combinations, writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Graph initGraph(int numberVertices, LinkedList<EdgeSimple> edges){
        Graph graph = new Graph(numberVertices);
        for(EdgeSimple edge : edges){
            if(edge.getSource() > numberVertices || edge.getDestination() > numberVertices){
                throw new NullPointerException();
            }
            System.out.println(edges.size() + "  " + edge.getDestination() + "   " + edge.getSource());
            graph.addEdge(edge);
        }
        return graph;
    }
}

