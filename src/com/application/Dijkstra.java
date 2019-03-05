package com.application;

import java.util.LinkedList;
import java.util.PriorityQueue;

public class Dijkstra {
    public void calculate(Vertex source){
        source.setMinDistance(0);
        PriorityQueue<Vertex> queue = new PriorityQueue<>();
        queue.add(source);
        while(!queue.isEmpty()){
            Vertex notVisitedVertex = queue.remove();
            for(Edge neighbour : notVisitedVertex.getNeighbours()){
                double newDistance = notVisitedVertex.getMinDistance() + neighbour.getWeight();
                if(neighbour.getVertex().getMinDistance() > newDistance){
                    queue.remove(neighbour.getVertex());
                    neighbour.getVertex().setMinDistance(newDistance);
                    neighbour.getVertex().setPath(new LinkedList<>(notVisitedVertex.getPath()));
                    neighbour.getVertex().getPath().add(notVisitedVertex);
                    queue.add(neighbour.getVertex());
                }
            }
        }
    }
}