package com.coverage.models;

public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
    	setX(x);
    	setY(y);
    }
    
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    @Override
    public String toString() {
    	return "(" + x + ", " + y + ")";
    }
    
    @Override
    public int hashCode() {
    	return (int) (x + y * 127);
    }
    
    @Override
    public boolean equals(Object obj) {
    	if(obj instanceof Point) {
    		return ((Point) obj).getX() == this.x && ((Point) obj).getY() == this.y;
    	}
    	
    	return false;
    }
}
