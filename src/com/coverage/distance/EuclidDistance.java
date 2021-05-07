package com.coverage.distance;

import com.coverage.models.Point;

public class EuclidDistance implements IDistance{
	@Override
	public double caculate(Point a, Point b) {
		double x = a.getX()-b.getX();
        double y = a.getY()-b.getY();
        
        return Math.sqrt(x*x+y*y);
	}
}
