package com.coverage.distance;

import com.coverage.models.Point;

public interface IDistance {
	/**
	 * Method perform calculations about two Point and return distance about them
	 */
	public double caculate(Point a, Point b);
}
