package com.application;

public class Passenger {
    private int index;
    private Position position;

    public Passenger(Position position){
        this.position = position;
    }

    public Passenger(Passenger passenger){
        this.index = passenger.getIndex();
        this.position = passenger.getPosition();
    }

    public Passenger(int index, Position position){
        this.index = index;
        this.position = position;
    }

    public int getIndex() {
        return index;
    }

    public Position getPosition() {
        return position;
    }

}