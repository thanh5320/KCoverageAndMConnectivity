package com.coverage.algorithm;

import java.util.Set;

import com.coverage.models.Relay;
import com.coverage.models.Sensor;

public interface Algorithms {
	/**
	 * The method is main of algorithms
	 */
	public void run (Set<Sensor> resultSensors, Set<Relay> resultRelays);
}
