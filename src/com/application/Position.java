package com.application;

public class Position {
    private int start;
    private int end;

    Position(int start, int end){
        this.start = start;
        this.end = end;
    }

    public int getEnd() {
        return end;
    }

    public int getStart() {
        return start;
    }
}