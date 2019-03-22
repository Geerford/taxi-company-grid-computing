package com.application;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.minBy;

public class App {

    public static LinkedList<Combination> collectOutputFiles(int numberFiles){
        int count = 0;
        Collector collector = new Collector();
        try {
            for(int i = 1; i <= numberFiles; i++){
                JsonReader reader = new JsonReader(new FileReader(String.format("output-%s.json", i)));

                Type collectionType = new TypeToken<ArrayList<Combination>>(){}.getType();

                List<Combination> task = new ArrayList<>(new Gson().fromJson(reader, collectionType));
                collector.addTask(task);
                ++count;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(count == numberFiles){
            List<Combination> combinations = getMinCombinations(collector);
            return compareTaxis(combinations);
        }
        else{
            return null;
        }
    }

    public static int generateTasks() {
        Field field = Init.getField();
        int countCombination = (int)Math.pow(2, field.getNumberPassengers()) - 1; //O(2^N - 1)
        int taskCount = 0;
        createJob();
        int numberVertices = Init.getNumberVertices();
        LinkedList<EdgeSimple> graph = Init.getEdges();
        for (Taxi taxi : field.getTaxis()){
            List<LinkedList<Integer>> combinations = new ArrayList<>();
            //Generate combination
            int count = 0, chunk = 50000, remainder, maxSizeCombinations = 0;
            while(true){
                maxSizeCombinations += chunk;
                if(countCombination - maxSizeCombinations < chunk){
                    remainder = countCombination - maxSizeCombinations - 1;
                    break;
                }
            }
            List<LinkedList<Integer>> combinationsResult = new ArrayList<>();
            for(int i = 1; i <= countCombination; i++){
                LinkedList<Integer> path = new LinkedList<>(bitPositions(i));
                combinations.add(path);
                if(combinationsResult.size() == maxSizeCombinations + 1 && remainder > 0){
                    --remainder;
                }
                boolean checker = false;
                if(countCombination < chunk && i == countCombination){
                    checker = true;
                }
                if(countCombination > chunk && count == chunk){
                    combinationsResult.addAll(combinations);
                    combinations.clear();
                    count = 0;
                }
                else if(remainder == 0){ //Remainder of division
                    combinationsResult.addAll(combinations);
                }
                else if(checker){ //Combinations is less than chunk
                    combinationsResult.addAll(combinations);
                }
                ++count;
            }
            //Divide combinations into parts for transfer and normalize it
            for(int i = 0; i <= combinationsResult.size(); i+=chunk){
                if(i + chunk > combinationsResult.size()){
                    if(i < chunk){
                        InputTask task = new InputTask(field.getPassengers(), new ArrayList<>(combinationsResult),
                                taxi.getIndex(), taxi.getWeight(), numberVertices, graph);
                        String fileName = String.format("input-%s.json", ++taskCount);
                        writeTask(taskCount);
                        saveToJson(fileName, task);
                    }
                    else{
                        InputTask newTaskTest = new InputTask(field.getPassengers(),
                                new ArrayList<>(combinationsResult.subList(i-chunk, combinationsResult.size())),
                                taxi.getIndex(),taxi.getWeight(), numberVertices, graph);
                        String fileName = String.format("input-%s.json", ++taskCount);
                        writeTask(taskCount);
                        saveToJson(fileName, newTaskTest);
                    }
                }
                else{
                    InputTask newTaskTest = new InputTask(field.getPassengers(),
                            new ArrayList<>(combinationsResult.subList(i, i + chunk)), taxi.getIndex(), taxi.getWeight(),
                            numberVertices, graph);
                    String fileName = String.format("input-%s.json", ++taskCount);
                    writeTask(taskCount);
                    saveToJson(fileName, newTaskTest);
                }
            }
        }
        return taskCount;
    }

    private static void createJob(){
        try {
            Files.write(Paths.get("TaxiJob.jdf"), Arrays.asList("job:", "label: TaxiJob"),
                    Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeTask(int number){
        List<String> lines = new ArrayList<>();
        lines.add("task:");
        lines.add("init: store app.jar app.jar");
        lines.add(String.format("store input-%s.json input-%s.json", number, number));
        lines.add(String.format("remote: java -jar app.jar input-%s.json output-$TASK.json", number));
        lines.add("final: get output-$TASK.json output-$TASK.json");
        try {
            Files.write(Paths.get("TaxiJob.jdf"), lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveToJson(String fileName, InputTask task){
        try (Writer writer = new FileWriter(fileName)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(task, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static LinkedList<Combination> compareTaxis(List<Combination> combinations){
        LinkedList<Integer> notVisitedPassengers = new LinkedList<>();
        int numberPassengers = Init.getNumberPassengers();
        for(int i = 0; i < numberPassengers; i++){
            notVisitedPassengers.addLast(i);
        }
        LinkedList<Combination> best = new LinkedList<>();
        Combination bestCombination = combinations.get(combinations.size() - 1);
        LinkedList<LinkedList<Combination>> betterCombinationsList = new LinkedList<>();
        for(int i = combinations.size() - 2; i >= 0; i--){
            LinkedList<Combination> betterCombinations = new LinkedList<>();
            LinkedList<Integer> notVisitedPassengersTemp = new LinkedList<>(notVisitedPassengers);
            var current = combinations.get(i);
            betterCombinations.addLast(current);
            var currentPassengers = current.getPassengers();
            for(var item : currentPassengers){
                notVisitedPassengersTemp.remove(item);
            }
            LinkedList<Integer> currentTaxis = new LinkedList<>();
            currentTaxis.add(current.getTaxiIndex());
            int checker = current.getPassengers().size();
            //Get all not visited combinations of desired length.
            while(notVisitedPassengersTemp.size() > 0){
                var remainder = notVisitedPassengersTemp.getFirst();
                var remainderCombination = combinations.stream().filter(c -> c.getPassengers()
                        .contains(remainder) && c.getPassengers().size() <= notVisitedPassengersTemp.size())
                        .collect(Collectors.toList());
                notVisitedPassengersTemp.remove();
                for(Combination combination : remainderCombination){
                    boolean check = true;
                    for(Integer passenger : combination.getPassengers()){
                        if(notVisitedPassengersTemp.contains(passenger)){
                            check = false;
                        }
                    }
                    if(check && !currentTaxis.contains(combination.getTaxiIndex())){
                        checker += combination.getPassengers().size();
                        betterCombinations.addLast(combination);
                        currentTaxis.add(combination.getTaxiIndex());
                    }
                }
            }
            //Check passages for non-repetition.
            boolean check = true;
            LinkedList<Integer> visitedPassengers = new LinkedList<>();
            for(var combination : betterCombinations){
                for(Integer item : combination.getPassengers()){
                    if(visitedPassengers.contains(item)){
                        check = false;
                    }
                    else{
                        visitedPassengers.add(item);
                    }
                }
            }
            if(check && checker == bestCombination.getPassengers().size()){
                betterCombinationsList.add(betterCombinations);
            }
        }
        //Get best combinations.
        LinkedList<Combination> bestCombinationsTemp = new LinkedList<>();
        double bestWeight = Double.POSITIVE_INFINITY;
        for(var combinationList : betterCombinationsList){
            double bestWeightTemp = 0;
            LinkedList<Combination> bestCombinationTemp = new LinkedList<>();
            for(var betterCombinations : combinationList){
                bestWeightTemp += betterCombinations.getCost();
                bestCombinationTemp.add(betterCombinations);
            }
            if(bestWeightTemp < bestWeight){
                bestCombinationsTemp.clear();
                bestCombinationsTemp.addAll(bestCombinationTemp);
                bestWeight = bestWeightTemp;
            }
        }
        if(bestCombinationsTemp.size() == 0){
            best.add(bestCombination);
        }
        else{
            best.addAll(bestCombinationsTemp);
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