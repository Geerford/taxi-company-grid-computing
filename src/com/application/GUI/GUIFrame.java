package com.application.GUI;

import com.application.*;
import mdlaf.MaterialLookAndFeel;
import mdlaf.animation.MaterialUIMovement;
import mdlaf.utils.MaterialColors;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class GUIFrame {
    private JPanel rootPanel;
    private JTabbedPane tabbedPanel;
    private JTable taxisTable;
    private JTable passengersTable;
    private JTable graphTable;
    private JTabbedPane graphTabbedPane;
    private JTextField graphAddVertexATextField;
    private JTextField graphAddVertexBTextField;
    private JTextField graphAddWeightTextField;
    private JButton graphAddButton;
    private JTabbedPane taxisTabbedPane;
    private JTextField taxisAddLocationTextField;
    private JButton taxisAddButton;
    private JTabbedPane passengersTabbedPane;
    private JTextField passengersAddLocationTextField;
    private JTextField passengersAddArrivalTextField;
    private JButton passengersAddButton;
    private JTextField taxisEditIdTextField;
    private JTextField taxisEditLocationTextField;
    private JButton taxisEditButton;
    private JTextField taxisDeleteIdTextField;
    private JButton taxisDeleteButton;
    private JTextField passengersEditIdTextField;
    private JTextField passengersEditLocationTextField;
    private JTextField passengersEditArrivalTextField;
    private JButton passengersEditButton;
    private JTextField passengersDeleteIdTextField;
    private JButton passengersDeleteButton;
    private JTextField graphEditVertexATextField;
    private JTextField graphEditVertexBTextField;
    private JTextField graphEditWeightTextField;
    private JButton graphEditButton;
    private JTextField graphDeleteVertexATextField;
    private JTextField graphDeleteVertexBTextField;
    private JButton graphDeleteButton;
    private JButton solveJobButton;
    private JLabel statusLabel;
    private JTable tasksTable;
    private JPanel taxisTab;
    private JPanel passengersTab;
    private JPanel graphTab;
    private JPanel tasksTab;
    private JPanel taxisAddTab;
    private JPanel taxisEditTab;
    private JPanel taxisDeleteTab;
    private JPanel passengersAddTab;
    private JPanel passengersEditTab;
    private JPanel passengersDeleteTab;
    private JPanel graphAddTab;
    private JPanel graphEditTab;
    private JPanel graphDeleteTab;
    private JLabel passengersAddLocationLabel;
    private JLabel passengersArrivalLabel;
    private JLabel taxisAddLocationLabel;
    private JLabel taxisEditIdLabel;
    private JLabel taxisEditLocationLabel;
    private JLabel taxisDeleteIdLabel;
    private JScrollPane taxisScrollPane;
    private JLabel passengersEditIdLabel;
    private JLabel passengersEditLocationLabel;
    private JLabel passengersEditArrivalLabel;
    private JLabel passengersDeleteIdLabel;
    private JScrollPane passengersScrollPane;
    private JScrollPane graphScrollPane;
    private JLabel graphAddVertexALabel;
    private JLabel graphAddVertexBLabel;
    private JLabel graphAddWeightLabel;
    private JLabel graphEditVertexALabel;
    private JLabel graphEditVertexBLabel;
    private JLabel graphEditWeightLabel;
    private JLabel graphDeleteVertexALabel;
    private JLabel graphDeleteVertexBLabel;
    private JScrollPane tasksScrollPane;
    private static int countTasks;

    public GUIFrame() {
        JFrame frame = new JFrame("Taxi company");
        frame.setMinimumSize (new Dimension (600, 400));
        frame.setContentPane(rootPanel);
        //MATERIAL BUTTONS
        MaterialUIMovement.add (taxisAddButton, MaterialColors.BLUE_GRAY_200);
        MaterialUIMovement.add (passengersAddButton, MaterialColors.BLUE_GRAY_200);
        MaterialUIMovement.add (graphAddButton, MaterialColors.BLUE_GRAY_200);
        MaterialUIMovement.add (taxisEditButton, MaterialColors.BLUE_GRAY_200);
        MaterialUIMovement.add (passengersEditButton, MaterialColors.BLUE_GRAY_200);
        MaterialUIMovement.add (graphEditButton, MaterialColors.BLUE_GRAY_200);
        MaterialUIMovement.add (taxisDeleteButton, MaterialColors.BLUE_GRAY_200);
        MaterialUIMovement.add (passengersDeleteButton, MaterialColors.BLUE_GRAY_200);
        MaterialUIMovement.add (graphDeleteButton, MaterialColors.BLUE_GRAY_200);
        MaterialUIMovement.add (solveJobButton, MaterialColors.BLUE_GRAY_200);
        //INIT TABLES
        taxisTable.setModel(initTaxis());
        passengersTable.setModel(initPassengers());
        graphTable.setModel(initGraph());
        //INIT FRAME
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //RESULTS
        solveJobButton.addActionListener(e -> {
            deleteFiles();
            //Generate new task
            countTasks = App.generateTasks();
            ProcessBuilder builder = new ProcessBuilder("bash", "-c", "../bin/mygrid addjob TaxiJob.jdf");
            try {
                Process process = builder.start();
                process.waitFor();
            } catch (IOException | InterruptedException e1) {
                e1.printStackTrace();
            }
            //Collect all tasks
            if(countTasks > 0){
                LinkedList<Combination> bestCombinations = App.collectOutputFiles(countTasks);
                if(bestCombinations != null){
                    String[] columnNames = {"Taxi", "Passengers", "Path", "Cost"};
                    Object[][] data = new Object[bestCombinations.size()][columnNames.length];
                    DefaultTableModel tableModel = new DefaultTableModel();
                    for(var i : columnNames){
                        tableModel.addColumn(i);
                    }
                    List<String> lines = new ArrayList<>();
                    for(int i = 0; i < bestCombinations.size(); i++){
                        data[i][0] = bestCombinations.get(i).getTaxiIndex();
                        data[i][1] = bestCombinations.get(i).getPassengers();
                        data[i][2] = bestCombinations.get(i).getPath();
                        data[i][3] = bestCombinations.get(i).getCost();
                        tableModel.addRow(data[i]);
                        lines.add("Taxi№" + bestCombinations.get(i).getTaxiIndex() + " || Passengers: " +
                                bestCombinations.get(i).getPassengers() + " || Path: " +
                                bestCombinations.get(i).getPath() + " = " + bestCombinations.get(i).getCost());
                    }
                    tasksTable.setModel(tableModel);

                    try {
                        Files.write(Paths.get("result.txt"), lines, Charset.forName("UTF-8"));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                else{
                    statusLabel.setText("The number of tasks does not match");
                }
            }
        });
        //TAXIS
        taxisAddButton.addActionListener(e -> {
            try{
                Field field = Init.getField();
                int location = Integer.parseInt(taxisAddLocationTextField.getText());
                if(location > Init.getNumberVertices()){
                    throw new Exception();
                }
                field.getTaxis().add(new Taxi(Init.getNumberTaxis(), location));
                Init.setNumberTaxis(field.getNumberTaxis() + 1);
                field.setNumberTaxis(field.getNumberTaxis() + 1);
                Init.setField(field);
            }
            catch (Exception ex){
                statusLabel.setText("Error");
            }
            finally {
                taxisAddLocationTextField.setText("");
                taxisTable.setModel(initTaxis());
            }
        });
        taxisEditButton.addActionListener(e -> {
            try{
                Field field = Init.getField();
                int id = Integer.parseInt(taxisEditIdTextField.getText()), location = Integer.parseInt(taxisEditLocationTextField.getText());
                if(id > Init.getNumberTaxis() || location > Init.getNumberVertices()){
                    throw new Exception();
                }
                field.getTaxis().get(id).setWeight(location);
                Init.setField(field);
            }
            catch (Exception ex){
                statusLabel.setText("Error");
            }
            finally {
                taxisEditIdTextField.setText("");
                taxisEditLocationTextField.setText("");
                taxisTable.setModel(initTaxis());
            }
        });
        taxisDeleteButton.addActionListener(e -> {
            try{
                Field field = Init.getField();
                int id = Integer.parseInt(taxisDeleteIdTextField.getText());
                if(id > Init.getNumberTaxis()){
                    throw new Exception();
                }
                field.getTaxis().remove(id);
                Init.setNumberTaxis(field.getNumberTaxis() - 1);
                field.setNumberTaxis(field.getNumberTaxis() - 1);
                for(int i = id; i <= Init.getNumberTaxis(); i++){
                    var taxi = field.getTaxis().get(i);
                    taxi.setIndex(i);
                }
                Init.setField(field);
            }
            catch (Exception ex){
                statusLabel.setText("Error");
            }
            finally {
                taxisDeleteIdTextField.setText("");
                taxisTable.setModel(initTaxis());
            }
        });
        //PASSENGERS
        passengersAddButton.addActionListener(e -> {
            try{
                Field field = Init.getField();
                if(Integer.parseInt(passengersAddLocationTextField.getText()) > Init.getNumberVertices() ||
                        Integer.parseInt(passengersAddArrivalTextField.getText()) > Init.getNumberVertices()){
                    throw new Exception();
                }
                field.getPassengers().add(new Passenger(Init.getNumberPassengers(),
                        new Position(Integer.parseInt(passengersAddLocationTextField.getText()), Integer.parseInt(passengersAddArrivalTextField.getText()))));
                Init.setNumberPassengers(field.getNumberPassengers() + 1);
                field.setNumberPassengers(field.getNumberPassengers() + 1);
                Init.setField(field);
            }
            catch (Exception ex){
                statusLabel.setText("Error");
            }
            finally {
                passengersAddLocationTextField.setText("");
                passengersAddArrivalTextField.setText("");
                passengersTable.setModel(initPassengers());
            }
        });
        passengersEditButton.addActionListener(e -> {
            try{
                int id = Integer.parseInt(passengersEditIdTextField.getText()), location = Integer.parseInt(passengersEditLocationTextField.getText()),
                        arrival = Integer.parseInt(passengersEditArrivalTextField.getText());
                Field field = Init.getField();
                if(id > Init.getNumberPassengers() || location > Init.getNumberVertices() ||
                        arrival > Init.getNumberVertices()){
                    throw new Exception();
                }
                field.getPassengers().get(id).setPosition(new Position(location, arrival));
                Init.setField(field);
            }
            catch (Exception ex){
                statusLabel.setText("Error");
            }
            finally {
                passengersEditIdTextField.setText("");
                passengersEditLocationTextField.setText("");
                passengersEditArrivalTextField.setText("");
                passengersTable.setModel(initPassengers());
            }
        });
        passengersDeleteButton.addActionListener(e -> {
            Field field = Init.getField();
            try{
                int id = Integer.parseInt(passengersDeleteIdTextField.getText());
                if(id > Init.getNumberPassengers()){
                    throw new Exception();
                }
                field.getPassengers().remove(id);
                Init.setNumberPassengers(field.getNumberPassengers() - 1);
                field.setNumberPassengers(field.getNumberPassengers() - 1);
                for(int i = id; i <= Init.getNumberPassengers(); i++){
                    var passenger = field.getPassengers().get(i);
                    passenger.setIndex(i);
                }
                Init.setField(field);
            }
            catch (Exception ex){
                statusLabel.setText("Error");
            }
            finally {
                passengersDeleteIdTextField.setText("");
                passengersTable.setModel(initPassengers());
            }
        });
        //GRAPH
        graphAddButton.addActionListener(e -> {
            try{
                LinkedList<EdgeSimple> edges = Init.getEdges();
                int a = Integer.parseInt(graphAddVertexATextField.getText()), b = Integer.parseInt(graphAddVertexBTextField.getText()),
                        w = Integer.parseInt(graphAddWeightTextField.getText());
                if(a != b && !Init.isInGraph(a, b)){
                    int indexA = a, indexB = b;
                    if(a > Init.getNumberVertices()){
                        indexA = Init.getNumberVertices();
                        Init.setNumberVertices(Init.getNumberVertices()+1);
                    }
                    if(b > Init.getNumberVertices()){
                        indexB = Init.getNumberVertices();
                        Init.setNumberVertices(Init.getNumberVertices()+1);
                    }
                    edges.add(new EdgeSimple(indexA, indexB, w));
                    edges.add(new EdgeSimple(indexB, indexA, w));
                }
                else{
                    throw new Exception();
                }
            }
            catch (Exception ex){
                statusLabel.setText("Error");
            }
            finally {
                graphAddVertexATextField.setText("");
                graphAddVertexBTextField.setText("");
                graphAddWeightTextField.setText("");
                graphTable.setModel(initGraph());
            }
        });
        graphEditButton.addActionListener(e -> {
            try{
                LinkedList<EdgeSimple> edges = Init.getEdges();
                int a = Integer.parseInt(graphEditVertexATextField.getText()),
                        b = Integer.parseInt(graphEditVertexBTextField.getText()),
                        w = Integer.parseInt(graphEditWeightTextField.getText());
                if(a == b){
                    throw new Exception();
                }
                for(var edge : edges){
                    if(edge.getSource() == a && edge.getDestination() == b || edge.getSource() == b &&
                            edge.getDestination() == a ){
                        edge.setWeight(w);
                    }
                }
            }
            catch (Exception ex){
                statusLabel.setText("Error");
            }
            finally {
                graphEditVertexATextField.setText("");
                graphEditVertexBTextField.setText("");
                graphEditWeightTextField.setText("");
                graphTable.setModel(initGraph());
            }
        });
        graphDeleteButton.addActionListener(e -> {
            try{
                LinkedList<EdgeSimple> edges = Init.getEdges();
                int a = Integer.parseInt(graphDeleteVertexATextField.getText()),
                        b = Integer.parseInt(graphDeleteVertexBTextField.getText());
                if(a == b){
                    throw new Exception();
                }
                edges.removeIf(edge -> edge.getSource() == a && edge.getDestination() == b || edge.getSource() == b &&
                        edge.getDestination() == a);
            }
            catch (Exception ex){
                statusLabel.setText("Error");
            }
            finally {
                graphDeleteVertexATextField.setText("");
                graphDeleteVertexBTextField.setText("");
                graphTable.setModel(initGraph());
            }
        });
        //STATUS
        ActionListener statusListener = e -> {
            statusLabel.setText("");
        };
        Timer timer = new Timer(3000,statusListener);
        timer.start();
    }

    public static void main(String[] args) {
        Init.createGraph();
        Init.initField();
        try {
            UIManager.setLookAndFeel (new MaterialLookAndFeel ());
        }
        catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace ();
        }
        new GUIFrame();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                deleteFiles();
            }
        }, "Shutdown-thread"));
    }

    private static void deleteFiles(){
        //Delete old files
        File file;
        if(countTasks > 0){
            for(int i = 1; i <= countTasks; i++){
                file = new File(String.format("output-%s.json", i));
                if(file.exists() && !file.isDirectory()) {
                    file.delete();
                }
                file = new File(String.format("input-%s.json", i));
                if(file.exists() && !file.isDirectory()) {
                    file.delete();
                }
            }
        }
        file = new File("result.txt");
        if(file.exists() && !file.isDirectory()) {
            file.delete();
        }
        file = new File("TaxiJob.jdf");
        if(file.exists() && !file.isDirectory()) {
            file.delete();
        }
    }

    public DefaultTableModel initTaxis(){
        var list = Init.getField().getTaxis();
        String[] columnNames = {"ID", "Location"};
        Object[][] data = new Object[list.size()][columnNames.length];
        DefaultTableModel tableModel = new DefaultTableModel();
        for(var i : columnNames){
            tableModel.addColumn(i);
        }
        for(int i = 0; i < list.size(); i++){
            data[i][0] = list.get(i).getIndex();
            data[i][1] = list.get(i).getWeight();
            tableModel.addRow(data[i]);
        }
        return tableModel;
    }

    public DefaultTableModel initPassengers(){
        var list = Init.getField().getPassengers();

        String[] columnNames = {"ID", "Location", "Arrival"};
        Object[][] data = new Object[list.size()][columnNames.length];
        DefaultTableModel tableModel = new DefaultTableModel();
        for(var i : columnNames){
            tableModel.addColumn(i);
        }
        for(int i = 0; i < list.size(); i++){
            data[i][0] = list.get(i).getIndex();
            data[i][1] = list.get(i).getPosition().getStart();
            data[i][2] = list.get(i).getPosition().getEnd();
            tableModel.addRow(data[i]);
        }
        return tableModel;
    }

    public DefaultTableModel initGraph(){
        LinkedList<EdgeSimple> edges = Init.getEdges();
        edges.sort(Comparator.comparing(EdgeSimple::getSource));
        String[] columnNames = {"Vertex (A)", "Vertex (B)", "Weight"};
        Object[][] data = new Object[edges.size()][columnNames.length];
        DefaultTableModel tableModel = new DefaultTableModel();
        for(var i : columnNames){
            tableModel.addColumn(i);
        }
        for(int i = 0; i < edges.size(); i++){
            data[i][0] = edges.get(i).getSource();
            data[i][1] = edges.get(i).getDestination();
            data[i][2] = edges.get(i).getWeight();
            tableModel.addRow(data[i]);
        }
        return tableModel;
    }
}
