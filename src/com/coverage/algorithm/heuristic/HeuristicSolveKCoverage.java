package com.coverage.algorithm.heuristic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.coverage.algorithm.Algorithms;
import com.coverage.algorithm.support.EquationCircle;
import com.coverage.distance.EuclidDistance;
import com.coverage.distance.IDistance;
import com.coverage.main.KM;
import com.coverage.models.Relay;
import com.coverage.models.Sensor;
import com.coverage.models.Target;

public class HeuristicSolveKCoverage implements Algorithms{
	private IDistance distance = new EuclidDistance();
	private SensorGeneration sensorGeneration = new SensorGeneration();
	private Set<Target> targets;
	
	public HeuristicSolveKCoverage(Set<Target> targets) {
		this.targets = targets;
	}
	
	/**
	 * Run algorithms
	 * @param url
	 * @return
	 */
	@Override
    public void run(Set<Sensor> resultSensors, Set<Relay> resultRelays){
    	// phase 1
        Set<Sensor> sensors = buildSensor(targets);
        HashMap<Sensor, Integer> mapWS = weightOfSensor(sensors, targets);
        LinkedHashMap<Sensor, Integer> sortedMap = sortHashMapByValue(mapWS);
        Set<Sensor> results = optimalSensor(sortedMap, targets);

        // phase 2 
        // ...
        
        // add result sensors
        resultSensors.addAll(results);
        
        // add result relays
//        resultRelays.addAll(results);
    }
	
	/**
	 * Phase 1, Step 1 :
	 * Build list sensor with k coverage
	 * 
	 * @param targets
	 * @return
	 */
	public Set<Sensor> buildSensor(Set<Target> targets) {
		Set<Sensor> sennors = new HashSet<>();
		List<Target> targetList = new ArrayList<>();

		targetList.addAll(targets);
		for (int i = 0; i < targetList.size() - 1; i++) {
			for (int j = i + 1; j < targetList.size(); j++) {
				List<Sensor> listSensor = intersection(targetList.get(i), targetList.get(j));

				if (listSensor == null) {
					// generation 2 * k point random for two target
					Set<Sensor> s1 = new HashSet<>();
					Set<Sensor> s2 = new HashSet<>();

					while (s1.size() < KM.K) {
						s1.add(sensorGeneration.compute(targetList.get(i)));
					}

					while (s2.size() < KM.K) {
						s2.add(sensorGeneration.compute(targetList.get(j)));
					}

					sennors.addAll(s1);
					sennors.addAll(s2);

				} else if (listSensor.size() == 1) {
					// generation 2 * (k - 1)point random for two target
					Set<Sensor> s1 = new HashSet<>();
					s1.addAll(listSensor);

					Set<Sensor> s2 = new HashSet<>();
					s2.addAll(listSensor);

					while (s1.size() < KM.K) {
						s1.add(sensorGeneration.compute(targetList.get(i)));
					}

					while (s2.size() < KM.K) {
						s2.add(sensorGeneration.compute(targetList.get(j)));
					}

					sennors.addAll(s1);
					sennors.addAll(s2);

				} else {
					// generation 2 * (k-2) point random for two target
					Set<Sensor> s = new HashSet<>();
					s.addAll(listSensor);

					while (s.size() < KM.K) {
						s.add(sensorGeneration.compute(targetList.get(i), targetList.get(j), listSensor.get(0),
								listSensor.get(1)));
					}
					sennors.addAll(s);
				}
			}
		}

		return sennors;
	}

	/**
	 * Phase 1, Step 2 : 
	 * Return Optimal set of sensor in phase 1
	 * @param sensors
	 * @param targets
	 * @return
	 */
    public static Set<Sensor> optimalSensor(LinkedHashMap<Sensor, Integer> sensors, Set<Target> targets){
        //Set<Map.Entry<Sensor, Integer>> sortedSensor= sensors.entrySet();
        HashMap<Target, Integer> mapTarget = new HashMap<>();
        Set<Target> checkMapTarget = new HashSet<>();
        Set<Sensor> sensorSet = new HashSet<>();
        for(Target t : targets){
            mapTarget.put(t, KM.K);
        }
        for (Map.Entry<Sensor, Integer> s : sensors.entrySet()) {
            if(checkMapTarget.size()==mapTarget.size()){
                break;
            }

            for(Map.Entry<Target, Integer> t : mapTarget.entrySet()){
                if(t.getKey().isCoverage(s.getKey()) && t.getValue()>0){
                    t.setValue(t.getValue()-1);
                    if(t.getValue()==0) {
                        checkMapTarget.add(t.getKey());
                    }
                    sensorSet.add(s.getKey());
                }
            }
        }
        return sensorSet;
    }
    
	/**
	 * The method return number of intersection of two circle
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	public List<Sensor> intersection(Target t1, Target t2) {
		List<Sensor> sensors = new ArrayList<>();
		if (distance.caculate(t1, t2) < 2 * KM.RS) {
			double a = EquationCircle.solveA(t1.getX(), t1.getY(), t2.getX(), t2.getY());
			double b = EquationCircle.solveB(t1.getX(), t1.getY(), t2.getX(), t2.getY());
			double c = EquationCircle.solveC(t1.getX(), t1.getY(), t2.getX(), t2.getY(), KM.RS);
			List<Double> listY = EquationCircle.solveQuadraticEquations(a, b, c);

			for (double y : listY) {
				double x = EquationCircle.sloveX(y, t1.getX(), t1.getY(), t2.getX(), t2.getY());
				sensors.add(new Sensor(x, y));
			}

			return sensors;
		} else if (distance.caculate(t1, t2) == 2 * KM.RS) {
			sensors.add(new Sensor((t1.getX() + t2.getX()) / 2, (t1.getY() + t2.getY()) / 2));
			return sensors;
		} else {
			return null;
		}
	}

	/**
	 * Sort with weight of sensor coverage target
	 * @param hashMap
	 * @return
	 */
    public static LinkedHashMap<Sensor, Integer> sortHashMapByValue(HashMap<Sensor, Integer> hashMap){
        Set<Map.Entry<Sensor, Integer>> entries = hashMap.entrySet();

        Comparator<Map.Entry<Sensor, Integer>> comparator = new Comparator<Map.Entry<Sensor, Integer>>() {
            @Override
            public int compare(Map.Entry<Sensor, Integer> e1, Map.Entry<Sensor, Integer> e2) {
                int v1 = e1.getValue();
                int v2 = e2.getValue();
                if(v1==v2) return 0;
                else if( v1>v2) return -1;
                else return 1;
            }
        };

        List<Map.Entry<Sensor, Integer>> listEntries = new ArrayList<>(entries);
        Collections.sort(listEntries, comparator);
        LinkedHashMap<Sensor, Integer> sortedMap = new LinkedHashMap<>(listEntries.size());
        for (Map.Entry<Sensor, Integer> entry : listEntries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        
        return sortedMap;
    }

	/**
	 * Calculates the weight or the number of targets that each sensor covers
	 * 
	 * @param sensors
	 * @param targets
	 * @return
	 */
	public HashMap<Sensor, Integer> weightOfSensor(Set<Sensor> sensors, Set<Target> targets) {
		HashMap<Sensor, Integer> mapWS = new HashMap<Sensor, Integer>();
		for (Sensor s : sensors) {
			int w = 0;
			for (Target t : targets) {
				if (t.isCoverage(s)) {
					w += 1;
				}
			}
			mapWS.put(s, w);
		}
		return mapWS;
	}

}
