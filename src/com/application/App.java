package com.application;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.minBy;

public class App {
    private static int countPassengers, countTaxis;

    public static void main(String[] args) {
        Graph graph = initGraph(6);
        Field field = initField();
        int countCombination = (int)Math.pow(2, field.getNumberPassengers()) - 1; //O(2^N - 1)

        List<InputTask> tasks = new ArrayList<>();
        for (Taxi taxi : field.getTaxis()){
            List<LinkedList<Integer>> combinations = new ArrayList<>();
            for(int i = 1; i <= countCombination; i++){
                LinkedList<Integer> path = new LinkedList<>();
                for (Integer position : bitPositions(i)) {
                    path.addFirst(position);
                }
                combinations.add(path);
            }
            int chunk = 8;
            for(int i = 0; i < countCombination; i+=chunk){
                while(i + chunk > combinations.size()){
                    combinations.add(null);
                }
                tasks.add(new InputTask(field.getPassengers(), new Graph(graph), new ArrayList<>(combinations.subList(i, i + chunk)),
                        taxi.getIndex(), taxi.getWeight()));
            }
        }

        LinkedList<OutputTask> outputTasks = new LinkedList<>();
        for(InputTask task : tasks){
            task.getGraph().clearGraph();
            com.client.App client = new com.client.App(task);
            OutputTask outputTask = client.perform();
            outputTasks.addLast(outputTask);
        }

        Collector collector = new Collector();

        for(OutputTask task : outputTasks){
            collector.addTask(task);
        }
        List<Combination> combinations = getMinCombinations(collector);
        //TODO DEBUG_START
        showDistance(combinations);
        //TODO DEBUG_END
        LinkedList<Combination> best = compareTaxis(combinations);
        //TODO DEBUG_START
        System.out.println("________________________");
        showDistance(best);
        //TODO DEBUG_END
    }

    private static Graph initGraph(int numberVertices){
        Graph graph = new Graph(numberVertices);
        graph.addEdge(0, 1, 7); graph.addEdge(0, 2, 9); graph.addEdge(0, 5, 14);
        graph.addEdge(1, 0, 7); graph.addEdge(1, 3, 15); graph.addEdge(1, 2, 10);
        graph.addEdge(2, 3, 11); graph.addEdge(2, 5, 2); graph.addEdge(2, 0, 9); graph.addEdge(2, 1, 10);
        graph.addEdge(3, 4, 6); graph.addEdge(3, 1, 15); graph.addEdge(3, 2, 11);
        graph.addEdge(4, 3, 6); graph.addEdge(4, 5, 9);
        graph.addEdge(5, 0, 14); graph.addEdge(5, 2, 2); graph.addEdge(5, 4, 9);
        return graph;
    }

    private static Field initField(){
        Taxi[] T = new Taxi[]{
                new Taxi(0),
                new Taxi(1),
                new Taxi(2),
                new Taxi(3),
                new Taxi(4),
                new Taxi(5),
                new Taxi(5) };
        Passenger[] P = new Passenger[] {
                new Passenger(new Position(0, 1)),
                new Passenger(new Position(2, 4)),
                new Passenger(new Position(2, 1)),
                new Passenger(new Position(5, 0)),
                new Passenger(new Position(5, 4)),
                new Passenger(new Position(5, 1)),
                new Passenger(new Position(4, 1))
        };
        countTaxis = T.length;
        countPassengers = P.length;
        return new Field(T, P);
    }

    private static void showDistance(List<Combination> combinations){
        for(Combination combination : combinations){
            System.out.println("Taxi№" + combination.getTaxiIndex() + " || Passengers: " + combination.getPassengers() +
                    " || Path: " + combination.getPath() + " = " + combination.getCost());
        }
    }

    private static LinkedList<Combination> compareTaxis(List<Combination> combinations){
        LinkedList<Integer> notVisitedPassengers = new LinkedList<>();
        for(int i = 0; i < countPassengers; i++){
            notVisitedPassengers.addLast(i);
        }
        LinkedList<Combination> best = new LinkedList<>();
        Combination bestCombination = combinations.get(combinations.size() - 1);
        for(int i = combinations.size() - 2; i >= 0; i--){
            LinkedList<Combination> betterCombinations = new LinkedList<>();
            LinkedList<Integer> notVisitedPassengersTemp = new LinkedList<>(notVisitedPassengers);
            var current = combinations.get(i);
            betterCombinations.addLast(current);
            var currentPassengers = current.getPassengers();
            double currentWeight = current.getCost();
            for(var item : currentPassengers){
                notVisitedPassengersTemp.remove(item);
            }
            int checker = current.getPassengers().size();
            while(notVisitedPassengersTemp.size() > 0){
                var remainder = notVisitedPassengersTemp.remove();
                var remainderCombination = combinations.stream().filter(c -> c.getPassengers()
                        .contains(remainder)).collect(Collectors.toList());
                for(Combination combination : remainderCombination){
                    if(combination.getTaxiIndex() != current.getTaxiIndex()){
                        currentWeight += combination.getCost();
                        betterCombinations.addLast(combination);
                        checker += combination.getPassengers().size();
                    }
                }
            }
            if(checker == bestCombination.getPassengers().size() && currentWeight < bestCombination.getCost()){
                best.addAll(betterCombinations);
            }
        }
        if(best.size() == 0){
            best.addLast(bestCombination);
        }
        return best;
    }

    private static List<Combination> getMinCombinations(Collector collector) {
        var groupingTasks = collector.getCombinations().stream().collect(Collectors.groupingBy(Combination :: getPassengers,
                minBy(Comparator.comparingDouble(Combination::getCost))));
        List<Combination> combinations = new ArrayList<>();
        for(var optionalCombination : groupingTasks.values()){
            optionalCombination.ifPresent(combinations::add);
        }
        combinations.sort(Comparator.comparing(p -> p.getPassengers().size()));
        return combinations;
    }

    private static List<Integer> bitPositions(int number) {
        List<Integer> positions = new ArrayList<>();
        int position = 1;
        while (number != 0) {
            if ((number & 1) != 0) {
                positions.add(position);
            }
            position++;
            number = number >>> 1;
        }
        return positions;
    }
}