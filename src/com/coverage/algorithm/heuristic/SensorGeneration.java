package com.coverage.algorithm.heuristic;

import java.util.*;

import com.coverage.main.KM;
import com.coverage.models.Sensor;
import com.coverage.models.Target;

public class SensorGeneration {
	/**
	 * A method add sensor for 2 targets: in case there are 2 intersecting points
	 */
	public Sensor compute(Target t1, Target t2, Sensor s1, Sensor s2) {
		double x0 = s1.getX() > s2.getX() ? s2.getX() : s1.getX();
		
		Random rd = new Random();
		Double nrd = rd.nextDouble();
		double x = x0 + nrd * Math.abs(s1.getX() - s2.getX());
		
		if (rd.nextBoolean()) {
			double yMax;
			double yMin;
			if (s1.getY() > s2.getY()) {
				yMax = s1.getX();
				yMin = s2.getY();
			} else {
				yMax = s2.getY();
				yMin = s1.getY();
			}
			double y = Math.sqrt(KM.RS * KM.RS - (x - t1.getX()) * (x - t1.getX())) + t1.getY();
			if (y <= yMax && y >= yMin) {
				return new Sensor(x, y);
			}
			y = -Math.sqrt(KM.RS * KM.RS - (x - t1.getX()) * (x - t1.getX())) + t1.getY();
			return new Sensor(x, y);
		} else {
			double yMax;
			double yMin;
			if (s1.getY() > s2.getY()) {
				yMax = s1.getX();
				yMin = s2.getY();
			} else {
				yMax = s2.getY();
				yMin = s1.getY();
			}
			double y = Math.sqrt(KM.RS * KM.RS - (x - t2.getX()) * (x - t2.getX())) + t2.getY();
			if (y <= yMax && y >= yMin) {
				return new Sensor(x, y);
			}
			y = -Math.sqrt(KM.RS * KM.RS - (x - t2.getX()) * (x - t2.getX())) + t2.getY();
			return new Sensor(x, y);
		}
	}

	/**
	 * The method find additional sensor coverage for a target: used in cases where there is no intersection 
	 * or there is 1 intersection
	 */
	public Sensor compute(Target t) {
		double x0 = t.getX() - KM.RS;
		
		Random rd = new Random();
		double nrd = rd.nextDouble();
		double x = x0 + nrd * 2 * KM.RS;
		
		if (rd.nextBoolean()) {
			double y = Math.sqrt(KM.RS * KM.RS - (x - t.getX()) * (x - t.getX())) + t.getY();
			return new Sensor(x, y);
		}
		
		double y = -Math.sqrt(KM.RS * KM.RS - (x - t.getX()) * (x - t.getX())) + t.getY();
		return new Sensor(x, y);
	}
}
