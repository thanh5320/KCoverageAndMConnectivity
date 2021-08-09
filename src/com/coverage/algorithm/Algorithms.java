	package com.coverage.algorithm;

import java.util.List;

import com.coverage.models.Relay;
import com.coverage.models.Sensor;

public interface Algorithms {
	/**
	 * The method is main of algorithms
	 * listResults include numOfSensors and numOfRelays
	 */
	public void run (List<Sensor> resultSensors, List<Relay> resultRelays, List<Integer> listReults);
}
