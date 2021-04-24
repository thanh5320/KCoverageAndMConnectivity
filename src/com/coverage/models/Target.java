package com.coverage.models;

import com.coverage.distance.EuclidDistance;
import com.coverage.distance.IDistance;
import com.coverage.main.KM;

public class Target extends  Point{
    public Target(double x, double y){
        super(x, y);
    }

    /**
     * Returns whether a sensor covers a target
     * @param s
     * @param t
     * @return boolean
     */
    public boolean isCoverage(Sensor s){
    	IDistance distance = new EuclidDistance();
        return distance.caculate(s,this) <= KM.RS + 0.00000001;
    }
}