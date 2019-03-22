package com.application;

import java.util.ArrayList;

public class Field {
    private int numberTaxis;
    private int numberPassengers;
    private ArrayList<Taxi> taxis;
    private ArrayList<Passenger> passengers;

    public Field(Taxi[] taxis, Passenger[] passengers){
        numberTaxis = taxis.length;
        numberPassengers = passengers.length;
        this.taxis = new ArrayList<>(numberTaxis);
        this.passengers = new ArrayList<>(numberPassengers);
        for(int i = 0; i < numberTaxis; i++){
            this.taxis.add(new Taxi(i, taxis[i].getWeight()));
        }
        for(int i = 0; i < numberPassengers; i++){
            this.passengers.add(new Passenger(i, passengers[i].getPosition()));
        }
    }

    public int getNumberTaxis(){
        return numberTaxis;
    }

    public void setNumberTaxis(int numberTaxis){
        this.numberTaxis = numberTaxis;
    }

    public int getNumberPassengers(){
        return numberPassengers;
    }

    public void setNumberPassengers(int numberPassengers){
        this.numberPassengers = numberPassengers;
    }

    public ArrayList<Taxi> getTaxis(){
        return taxis;
    }

    public ArrayList<Passenger> getPassengers(){
        return passengers;
    }
}
