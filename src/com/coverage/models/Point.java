package com.coverage.models;

import com.coverage.algorithm.support.Hash;
import com.coverage.distance.EuclidDistance;
import com.coverage.distance.IDistance;
import com.coverage.main.KM;

public class Point {
	public TypeOfPoint TYPE = TypeOfPoint.NONE;
	
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
    	return Hash.hashCode(x, y);
    }
    
    @Override
    public boolean equals(Object obj) {
    	IDistance distance = new EuclidDistance();
    	if(obj instanceof Point) {
//    		return ((Point) obj).getX() == this.x && ((Point) obj).getY() == this.y;
    		return distance.caculate(this, (Point) obj) < 0.01;
    	}
    	
    	return false;
    }
    
    /**
     * Returns whether a point covers a point
     */
    public boolean isCoverage(Point p){
    	IDistance distance = new EuclidDistance();
        return distance.caculate(p,this) <= KM.RS + 0.00000001;
    }
}
