package com.coverage.models;

public class Relay extends Point{
    public Relay(double x, double y){
        super(x, y);
        this.TYPE = TypeOfPoint.RELAY;
    }
}
