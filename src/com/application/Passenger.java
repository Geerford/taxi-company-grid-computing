package com.application;

public class Passenger {
    private int index;
    private Position position;

    public Passenger(Position position){
        this.position = position;
    }

    public Passenger(int index, Position position){
        this.index = index;
        this.position = position;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}