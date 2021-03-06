package com.application;

import java.util.ArrayList;
import java.util.LinkedList;

public class Graph {
    private ArrayList<Vertex> vertices;

    public Graph(Graph graph){
        this.vertices = new ArrayList<>(graph.getVertices());
    }

    public Graph(int numberVertices){
        vertices = new ArrayList<>(numberVertices);
        for(int i = 0; i < numberVertices; i++){
            vertices.add(new Vertex(i));
        }
    }

    public void addEdge(EdgeSimple edge){
        Vertex source = vertices.get(edge.getSource());
        source.getNeighbours().add(new Edge(vertices.get(edge.getDestination()), edge.getWeight()));
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    public Vertex getVertex(int vertex){
        return vertices.get(vertex);
    }

    public void clearGraph(){
        for(Vertex vertex : vertices){
            vertex.setPath(new LinkedList<>());
            vertex.setMinDistance(Double.POSITIVE_INFINITY);
        }

    }
}