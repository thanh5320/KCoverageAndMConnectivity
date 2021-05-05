package com.coverage.models;

import java.util.Set;

import com.coverage.main.KM;

public class Sensor extends Relay {
	// coverage radius
//	public static final double RS=5;
	
    public Sensor(double x, double y){
        super(x, y);
        this.TYPE = TypeOfPoint.SENSOR;
    }
    
    /**
     * Check a sensor have coverage same target with this object
     * @param sensor
     * @return
     */
    public boolean sameCoverage(Sensor sensor) {
    	Set<Target> targets = KM.TARGETS;
    	
    	if(targets == null) {
    		return false;
    	}
    	
    	if(targets.size() == 0) {
    		return false;
    	}
    	
    	for(Target target : targets) {
    		if(target.isCoverage(this) && target.isCoverage(sensor)) {
    			return true;
    		}
    	}
    	
    	return false;
    }
}
