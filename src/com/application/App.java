package com.application;

import com.google.gson.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.minBy;

public class App {
    private static int numberPassengers, numberTaxis, numberVertices = 100;

    public static void main(String[] args) {
        Field field = initField();
        int countCombination = (int)Math.pow(2, field.getNumberPassengers()) - 1; //O(2^N - 1)
        List<String> tasks = new ArrayList<>();
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
                LinkedList<EdgeSimple> graph = createGraph();
                if(i + chunk > combinationsResult.size()){
                    if(i < chunk){
                        InputTask task = new InputTask(field.getPassengers(),
                                new ArrayList<>(combinationsResult), taxi.getIndex(),
                                taxi.getWeight(), numberVertices, graph);
                        String fileName= "Output.json";
                        saveToJson(fileName, task);
                        tasks.add(new Gson().toJson(task));
                    }
                    else{
                        InputTask newTaskTest = new InputTask(field.getPassengers(),
                                new ArrayList<>(combinationsResult.subList(i-chunk, combinationsResult.size())), taxi.getIndex(),
                                taxi.getWeight(), numberVertices, graph);
                        String fileName= "Output.json";
                        saveToJson(fileName, newTaskTest);
                        tasks.add(new Gson().toJson(newTaskTest));
                    }
                }
                else{
                    InputTask newTaskTest = new InputTask(field.getPassengers(),
                            new ArrayList<>(combinationsResult.subList(i, i + chunk)), taxi.getIndex(), taxi.getWeight(),
                            numberVertices, graph);
                    String fileName= "Output.json";
                    saveToJson(fileName, newTaskTest);
                    tasks.add(new Gson().toJson(newTaskTest));
                }
            }
        }
        //Emulation of algorithm execution
        LinkedList<OutputTask> outputTasks = new LinkedList<>();
        for(String task : tasks){
            com.client.App client = new com.client.App(task);
            OutputTask outputTask = client.perform();
            outputTasks.addLast(outputTask);
        }
        //Collecting of results
        Collector collector = new Collector();

        for(OutputTask task : outputTasks){
            collector.addTask(task);
        }
        List<Combination> combinations = getMinCombinations(collector);
        showDistance(combinations);
        System.out.println("________________________");
        LinkedList<Combination> best = compareTaxis(combinations);
        showDistance(best);
    }

    private static void saveToJson(String fileName, InputTask task){
        try (Writer writer = new FileWriter(fileName)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(task, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static LinkedList<EdgeSimple> createGraph(){
        LinkedList<EdgeSimple> edges = new LinkedList<>();
        //№0
        edges.addLast(new EdgeSimple(0, 1,7));
        edges.addLast(new EdgeSimple(0, 2,9));
        edges.addLast(new EdgeSimple(0, 5,14));
        //№1
        edges.addLast(new EdgeSimple(1, 0,7));
        edges.addLast(new EdgeSimple(1, 3,15));
        edges.addLast(new EdgeSimple(1, 2,10));
        //№2
        edges.addLast(new EdgeSimple(2, 3,11));
        edges.addLast(new EdgeSimple(2, 5,2));
        edges.addLast(new EdgeSimple(2, 0,9));
        edges.addLast(new EdgeSimple(2, 1,10));
        //№3
        edges.addLast(new EdgeSimple(3, 4,6));
        edges.addLast(new EdgeSimple(3, 1,15));
        edges.addLast(new EdgeSimple(3, 2,11));
        //№4
        edges.addLast(new EdgeSimple(4, 3,6));
        edges.addLast(new EdgeSimple(4, 5,9));
        //№5
        edges.addLast(new EdgeSimple(5, 0,14));
        edges.addLast(new EdgeSimple(5, 2,2));
        edges.addLast(new EdgeSimple(5, 4,9));

        //№6
        edges.addLast(new EdgeSimple(6, 7,7));
        edges.addLast(new EdgeSimple(6, 8,9));
        edges.addLast(new EdgeSimple(6, 11,14));
        //№7
        edges.addLast(new EdgeSimple(7, 6,7));
        edges.addLast(new EdgeSimple(7, 9,15));
        edges.addLast(new EdgeSimple(7, 8,10));
        //№8
        edges.addLast(new EdgeSimple(8, 9,11));
        edges.addLast(new EdgeSimple(8, 11,2));
        edges.addLast(new EdgeSimple(8, 6,9));
        edges.addLast(new EdgeSimple(8, 7,10));
        //№9
        edges.addLast(new EdgeSimple(9, 10,6));
        edges.addLast(new EdgeSimple(9, 7,15));
        edges.addLast(new EdgeSimple(9, 8,11));
        //№10
        edges.addLast(new EdgeSimple(10, 9,6));
        edges.addLast(new EdgeSimple(10, 5,9));
        //№11
        edges.addLast(new EdgeSimple(11, 6,14));
        edges.addLast(new EdgeSimple(11, 8,2));
        edges.addLast(new EdgeSimple(11, 10,9));
        //№12
        edges.addLast(new EdgeSimple(12, 13,7));
        edges.addLast(new EdgeSimple(12, 14,9));
        edges.addLast(new EdgeSimple(12, 17,14));
        //№13
        edges.addLast(new EdgeSimple(13, 12,7));
        edges.addLast(new EdgeSimple(13, 15,15));
        edges.addLast(new EdgeSimple(13, 14,10));
        //№14
        edges.addLast(new EdgeSimple(14, 15,11));
        edges.addLast(new EdgeSimple(14, 17,2));
        edges.addLast(new EdgeSimple(14, 12,9));
        edges.addLast(new EdgeSimple(14, 13,10));
        //№15
        edges.addLast(new EdgeSimple(15, 16,6));
        edges.addLast(new EdgeSimple(15, 13,15));
        edges.addLast(new EdgeSimple(15, 14,11));
        //№16
        edges.addLast(new EdgeSimple(16, 15,6));
        edges.addLast(new EdgeSimple(16, 17,9));
        //№17
        edges.addLast(new EdgeSimple(17, 12,14));
        edges.addLast(new EdgeSimple(17, 14,2));
        edges.addLast(new EdgeSimple(17, 16,9));
        //№18
        edges.addLast(new EdgeSimple(18, 19,7));
        edges.addLast(new EdgeSimple(18, 20,9));
        edges.addLast(new EdgeSimple(18, 23,14));
        //№19
        edges.addLast(new EdgeSimple(19, 18,7));
        edges.addLast(new EdgeSimple(19, 21,15));
        edges.addLast(new EdgeSimple(19, 20,10));
        //№20
        edges.addLast(new EdgeSimple(20, 21,11));
        edges.addLast(new EdgeSimple(20, 23,2));
        edges.addLast(new EdgeSimple(20, 18,9));
        edges.addLast(new EdgeSimple(20, 19,10));
        //№21
        edges.addLast(new EdgeSimple(21, 22,6));
        edges.addLast(new EdgeSimple(21, 19,15));
        edges.addLast(new EdgeSimple(21, 20,11));
        //№22
        edges.addLast(new EdgeSimple(22, 21,6));
        edges.addLast(new EdgeSimple(22, 23,9));
        //№23
        edges.addLast(new EdgeSimple(23, 18,14));
        edges.addLast(new EdgeSimple(23, 20,2));
        edges.addLast(new EdgeSimple(23, 22,9));
        //№24
        edges.addLast(new EdgeSimple(24, 25,7));
        edges.addLast(new EdgeSimple(24, 26,9));
        edges.addLast(new EdgeSimple(24, 29,14));
        //№25
        edges.addLast(new EdgeSimple(25, 24,7));
        edges.addLast(new EdgeSimple(25, 27,15));
        edges.addLast(new EdgeSimple(25, 26,10));
        //№26
        edges.addLast(new EdgeSimple(26, 27,11));
        edges.addLast(new EdgeSimple(26, 29,2));
        edges.addLast(new EdgeSimple(26, 24,9));
        edges.addLast(new EdgeSimple(26, 25,10));
        //№27
        edges.addLast(new EdgeSimple(27, 28,6));
        edges.addLast(new EdgeSimple(27, 25,15));
        edges.addLast(new EdgeSimple(27, 26,11));
        //№28
        edges.addLast(new EdgeSimple(28, 27,6));
        edges.addLast(new EdgeSimple(28, 29,9));
        //№29
        edges.addLast(new EdgeSimple(29, 24,14));
        edges.addLast(new EdgeSimple(29, 26,2));
        edges.addLast(new EdgeSimple(29, 28,9));
        //№30
        edges.addLast(new EdgeSimple(30, 31,7));
        edges.addLast(new EdgeSimple(30, 32,9));
        edges.addLast(new EdgeSimple(30, 35,14));
        //№31
        edges.addLast(new EdgeSimple(31, 30,7));
        edges.addLast(new EdgeSimple(31, 33,15));
        edges.addLast(new EdgeSimple(31, 32,10));
        //№32
        edges.addLast(new EdgeSimple(32, 33,11));
        edges.addLast(new EdgeSimple(32, 35,2));
        edges.addLast(new EdgeSimple(32, 30,9));
        edges.addLast(new EdgeSimple(32, 31,10));
        //№33
        edges.addLast(new EdgeSimple(33, 34,6));
        edges.addLast(new EdgeSimple(33, 31,15));
        edges.addLast(new EdgeSimple(33, 32,11));
        //№34
        edges.addLast(new EdgeSimple(34, 33,6));
        edges.addLast(new EdgeSimple(34, 35,9));
        //№35
        edges.addLast(new EdgeSimple(35, 30,14));
        edges.addLast(new EdgeSimple(35, 32,2));
        edges.addLast(new EdgeSimple(35, 34,9));
        //№36
        edges.addLast(new EdgeSimple(36, 37,7));
        edges.addLast(new EdgeSimple(36, 38,9));
        edges.addLast(new EdgeSimple(36, 41,14));
        //№37
        edges.addLast(new EdgeSimple(37, 36,7));
        edges.addLast(new EdgeSimple(37, 39,15));
        edges.addLast(new EdgeSimple(37, 38,10));
        //№38
        edges.addLast(new EdgeSimple(38, 39,11));
        edges.addLast(new EdgeSimple(38, 41,2));
        edges.addLast(new EdgeSimple(38, 36,9));
        edges.addLast(new EdgeSimple(38, 37,10));
        //№39
        edges.addLast(new EdgeSimple(39, 40,6));
        edges.addLast(new EdgeSimple(39, 37,15));
        edges.addLast(new EdgeSimple(39, 38,11));
        //№40
        edges.addLast(new EdgeSimple(40, 39,6));
        edges.addLast(new EdgeSimple(40, 41,9));
        //№41
        edges.addLast(new EdgeSimple(41, 36,14));
        edges.addLast(new EdgeSimple(41, 38,2));
        edges.addLast(new EdgeSimple(41, 40,9));
        //№42
        edges.addLast(new EdgeSimple(42, 43,7));
        edges.addLast(new EdgeSimple(42, 44,9));
        edges.addLast(new EdgeSimple(42, 47,14));
        //№43
        edges.addLast(new EdgeSimple(43, 42,7));
        edges.addLast(new EdgeSimple(43, 45,15));
        edges.addLast(new EdgeSimple(43, 44,10));
        //№44
        edges.addLast(new EdgeSimple(44, 45,11));
        edges.addLast(new EdgeSimple(44, 47,2));
        edges.addLast(new EdgeSimple(44, 42,9));
        edges.addLast(new EdgeSimple(44, 43,10));
        //№45
        edges.addLast(new EdgeSimple(45, 46,6));
        edges.addLast(new EdgeSimple(45, 43,15));
        edges.addLast(new EdgeSimple(45, 44,11));
        //№46
        edges.addLast(new EdgeSimple(46, 45,6));
        edges.addLast(new EdgeSimple(46, 47,9));
        //№47
        edges.addLast(new EdgeSimple(47, 42, 14));
        edges.addLast(new EdgeSimple(47, 44,2));
        edges.addLast(new EdgeSimple(47, 46,9));
        //№48
        edges.addLast(new EdgeSimple(48, 49,7));
        edges.addLast(new EdgeSimple(48, 50,9));
        edges.addLast(new EdgeSimple(48, 53,14));
        //№49
        edges.addLast(new EdgeSimple(49, 48,7));
        edges.addLast(new EdgeSimple(49, 51,15));
        edges.addLast(new EdgeSimple(49, 50,10));
        //№50
        edges.addLast(new EdgeSimple(50, 51,11));
        edges.addLast(new EdgeSimple(50, 53,2));
        edges.addLast(new EdgeSimple(50, 48,9));
        edges.addLast(new EdgeSimple(50, 49,10));
        //№51
        edges.addLast(new EdgeSimple(51, 52,6));
        edges.addLast(new EdgeSimple(51, 49,15));
        edges.addLast(new EdgeSimple(51, 50,11));
        //№52
        edges.addLast(new EdgeSimple(52, 51,6));
        edges.addLast(new EdgeSimple(52, 53,9));
        //№53
        edges.addLast(new EdgeSimple(53, 48,14));
        edges.addLast(new EdgeSimple(53, 50,2));
        edges.addLast(new EdgeSimple(53, 52,9));
        //№54
        edges.addLast(new EdgeSimple(54, 55,7));
        edges.addLast(new EdgeSimple(54, 56,9));
        edges.addLast(new EdgeSimple(54, 59,14));
        //№55
        edges.addLast(new EdgeSimple(55, 54,7));
        edges.addLast(new EdgeSimple(55, 57,15));
        edges.addLast(new EdgeSimple(55, 56,10));
        //№56
        edges.addLast(new EdgeSimple(56, 57,11));
        edges.addLast(new EdgeSimple(56, 59,2));
        edges.addLast(new EdgeSimple(56, 54,9));
        edges.addLast(new EdgeSimple(56, 55,10));
        //№57
        edges.addLast(new EdgeSimple(57, 58,6));
        edges.addLast(new EdgeSimple(57, 55,15));
        edges.addLast(new EdgeSimple(57, 56,11));
        //№58
        edges.addLast(new EdgeSimple(58, 57,6));
        edges.addLast(new EdgeSimple(58, 59,9));
        //№59
        edges.addLast(new EdgeSimple(59, 54,14));
        edges.addLast(new EdgeSimple(59, 56,2));
        edges.addLast(new EdgeSimple(59, 58,9));
        //№60
        edges.addLast(new EdgeSimple(60, 61,7));
        edges.addLast(new EdgeSimple(60, 62,9));
        edges.addLast(new EdgeSimple(60, 65,14));
        //№61
        edges.addLast(new EdgeSimple(61, 60,7));
        edges.addLast(new EdgeSimple(61, 63,15));
        edges.addLast(new EdgeSimple(61, 62,10));
        //№62
        edges.addLast(new EdgeSimple(62, 63,11));
        edges.addLast(new EdgeSimple(62, 65,2));
        edges.addLast(new EdgeSimple(62, 60,9));
        edges.addLast(new EdgeSimple(62, 61,10));
        //№63
        edges.addLast(new EdgeSimple(63, 64,6));
        edges.addLast(new EdgeSimple(63, 61,15));
        edges.addLast(new EdgeSimple(63, 62,11));
        //№64
        edges.addLast(new EdgeSimple(64, 63,6));
        edges.addLast(new EdgeSimple(64, 65,9));
        //№65
        edges.addLast(new EdgeSimple(65, 60,14));
        edges.addLast(new EdgeSimple(65, 62,2));
        edges.addLast(new EdgeSimple(65, 64,9));
        //№66
        edges.addLast(new EdgeSimple(66, 67,7));
        edges.addLast(new EdgeSimple(66, 68,9));
        edges.addLast(new EdgeSimple(66, 71,14));
        //№67
        edges.addLast(new EdgeSimple(67, 66,7));
        edges.addLast(new EdgeSimple(67, 69,15));
        edges.addLast(new EdgeSimple(67, 68,10));
        //№68
        edges.addLast(new EdgeSimple(68, 69,11));
        edges.addLast(new EdgeSimple(68, 71,2));
        edges.addLast(new EdgeSimple(68, 66,9));
        edges.addLast(new EdgeSimple(68, 67,10));
        //№69
        edges.addLast(new EdgeSimple(69, 70,6));
        edges.addLast(new EdgeSimple(69, 67,15));
        edges.addLast(new EdgeSimple(69, 68,11));
        //№70
        edges.addLast(new EdgeSimple(70, 69,6));
        edges.addLast(new EdgeSimple(70, 71,9));
        //№71
        edges.addLast(new EdgeSimple(71, 66,14));
        edges.addLast(new EdgeSimple(71, 68,2));
        edges.addLast(new EdgeSimple(71, 70,9));
        //№72
        edges.addLast(new EdgeSimple(72, 73,7));
        edges.addLast(new EdgeSimple(72, 74,9));
        edges.addLast(new EdgeSimple(72, 77,14));
        //№73
        edges.addLast(new EdgeSimple(73, 72,7));
        edges.addLast(new EdgeSimple(73, 75,15));
        edges.addLast(new EdgeSimple(73, 74,10));
        //№74
        edges.addLast(new EdgeSimple(74, 75,11));
        edges.addLast(new EdgeSimple(74, 77,2));
        edges.addLast(new EdgeSimple(74, 72,9));
        edges.addLast(new EdgeSimple(74, 73,10));
        //№75
        edges.addLast(new EdgeSimple(75, 76,6));
        edges.addLast(new EdgeSimple(75, 73,15));
        edges.addLast(new EdgeSimple(75, 74,11));
        //№76
        edges.addLast(new EdgeSimple(76, 75,6));
        edges.addLast(new EdgeSimple(76, 77,9));
        //№77
        edges.addLast(new EdgeSimple(77, 72,14));
        edges.addLast(new EdgeSimple(77, 74,2));
        edges.addLast(new EdgeSimple(77, 76,9));
        //№78
        edges.addLast(new EdgeSimple(78, 79,7));
        edges.addLast(new EdgeSimple(78, 80,9));
        edges.addLast(new EdgeSimple(78, 83,14));
        //№79
        edges.addLast(new EdgeSimple(79, 78,7));
        edges.addLast(new EdgeSimple(79, 81,15));
        edges.addLast(new EdgeSimple(79, 80,10));
        //№80
        edges.addLast(new EdgeSimple(80, 81,11));
        edges.addLast(new EdgeSimple(80, 83,2));
        edges.addLast(new EdgeSimple(80, 78,9));
        edges.addLast(new EdgeSimple(80, 79,10));
        //№81
        edges.addLast(new EdgeSimple(81, 82,6));
        edges.addLast(new EdgeSimple(81, 79,15));
        edges.addLast(new EdgeSimple(81, 80,11));
        //№82
        edges.addLast(new EdgeSimple(82, 81,6));
        edges.addLast(new EdgeSimple(82, 83,9));
        //№83
        edges.addLast(new EdgeSimple(83, 78,14));
        edges.addLast(new EdgeSimple(83, 80,2));
        edges.addLast(new EdgeSimple(83, 82,9));
        //№84
        edges.addLast(new EdgeSimple(84, 85,7));
        edges.addLast(new EdgeSimple(84, 86,9));
        edges.addLast(new EdgeSimple(84, 89,14));
        //№85
        edges.addLast(new EdgeSimple(85, 84,7));
        edges.addLast(new EdgeSimple(85, 87,15));
        edges.addLast(new EdgeSimple(85, 86,10));
        //№86
        edges.addLast(new EdgeSimple(86, 87,11));
        edges.addLast(new EdgeSimple(86, 89,2));
        edges.addLast(new EdgeSimple(86, 84,9));
        edges.addLast(new EdgeSimple(86, 85,10));
        //№87
        edges.addLast(new EdgeSimple(87, 88,6));
        edges.addLast(new EdgeSimple(87, 85,15));
        edges.addLast(new EdgeSimple(87, 86,11));
        //№88
        edges.addLast(new EdgeSimple(88, 87,6));
        edges.addLast(new EdgeSimple(88, 89,9));
        //№89
        edges.addLast(new EdgeSimple(89, 84,14));
        edges.addLast(new EdgeSimple(89, 86,2));
        edges.addLast(new EdgeSimple(89, 88,9));
        //№90
        edges.addLast(new EdgeSimple(90, 91,7));
        edges.addLast(new EdgeSimple(90, 92,9));
        edges.addLast(new EdgeSimple(90, 95,14));
        //№91
        edges.addLast(new EdgeSimple(91, 90,7));
        edges.addLast(new EdgeSimple(91, 93,15));
        edges.addLast(new EdgeSimple(91, 92,10));
        //№92
        edges.addLast(new EdgeSimple(92, 93,11));
        edges.addLast(new EdgeSimple(92, 95,2));
        edges.addLast(new EdgeSimple(92, 90,9));
        edges.addLast(new EdgeSimple(92, 91,10));
        //№93
        edges.addLast(new EdgeSimple(93, 94,6));
        edges.addLast(new EdgeSimple(93, 91,15));
        edges.addLast(new EdgeSimple(93, 92,11));
        //№94
        edges.addLast(new EdgeSimple(94, 93,6));
        edges.addLast(new EdgeSimple(94, 95,9));
        //№95
        edges.addLast(new EdgeSimple(95, 90,14));
        edges.addLast(new EdgeSimple(95, 92,2));
        edges.addLast(new EdgeSimple(95, 94,9));
        //№96
        edges.addLast(new EdgeSimple(96, 1,7));
        edges.addLast(new EdgeSimple(1, 96,7));
        //№97
        edges.addLast(new EdgeSimple(97, 0,7));
        edges.addLast(new EdgeSimple(0, 97,7));
        //№98
        edges.addLast(new EdgeSimple(98, 3,11));
        edges.addLast(new EdgeSimple(3, 98,11));
        //№99
        edges.addLast(new EdgeSimple(99, 4,6));
        edges.addLast(new EdgeSimple(4, 99,6));
        //BRIDGES
        edges.addLast(new EdgeSimple(3, 87,1));
        edges.addLast(new EdgeSimple(87, 3,1));
        edges.addLast(new EdgeSimple(3, 39,1));
        edges.addLast(new EdgeSimple(39, 3,1));
        edges.addLast(new EdgeSimple(9, 21,1));
        edges.addLast(new EdgeSimple(21, 9,1));
        edges.addLast(new EdgeSimple(15, 87,1));
        edges.addLast(new EdgeSimple(87, 15,1));
        edges.addLast(new EdgeSimple(21, 81,1));
        edges.addLast(new EdgeSimple(81, 21,1));
        edges.addLast(new EdgeSimple(21, 27,1));
        edges.addLast(new EdgeSimple(27, 21,1));
        edges.addLast(new EdgeSimple(21, 57,1));
        edges.addLast(new EdgeSimple(57, 21,1));
        edges.addLast(new EdgeSimple(33, 39,1));
        edges.addLast(new EdgeSimple(39, 33,1));
        edges.addLast(new EdgeSimple(33, 69,1));
        edges.addLast(new EdgeSimple(69, 33,1));
        edges.addLast(new EdgeSimple(39, 45,1));
        edges.addLast(new EdgeSimple(45, 39,1));
        edges.addLast(new EdgeSimple(45, 75,1));
        edges.addLast(new EdgeSimple(75, 45,1));
        edges.addLast(new EdgeSimple(45, 93,1));
        edges.addLast(new EdgeSimple(93, 45,1));
        edges.addLast(new EdgeSimple(51, 57,1));
        edges.addLast(new EdgeSimple(57, 51,1));
        edges.addLast(new EdgeSimple(57, 63,1));
        edges.addLast(new EdgeSimple(63, 57,1));
        edges.addLast(new EdgeSimple(81, 87,1));
        edges.addLast(new EdgeSimple(87, 81,1));
        return edges;
    }

    private static Field initField(){
        Taxi[] T = new Taxi[]{
                new Taxi(0),
                new Taxi(1),
                new Taxi(2),
                new Taxi(3),
                new Taxi(4),
                new Taxi(5),
                new Taxi(5)
        };
        Passenger[] P = new Passenger[] {
                new Passenger(new Position(0, 1)),
                new Passenger(new Position(2, 4)),
                new Passenger(new Position(2, 1)),
                new Passenger(new Position(5, 0)),

                new Passenger(new Position(79, 12)),
                new Passenger(new Position(52, 66)),
                new Passenger(new Position(36, 4)),
                new Passenger(new Position(17, 82)),
                new Passenger(new Position(35, 11)),
                /*
                new Passenger(new Position(5, 4)),
                new Passenger(new Position(5, 1)),
                new Passenger(new Position(4, 1)),
                new Passenger(new Position(34, 21)),
                new Passenger(new Position(99, 1))*/

                //new Passenger(new Position(90, 99)),
                //new Passenger(new Position(44, 15)),
                //new Passenger(new Position(64, 1))
        };
        numberTaxis = T.length;
        numberPassengers = P.length;
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