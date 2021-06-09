package com.coverage.algorithm.heuristic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.coverage.algorithm.Algorithms;
import com.coverage.algorithm.support.Dijkstra;
import com.coverage.algorithm.support.EquationCircle;
import com.coverage.algorithm.support.Hash;
import com.coverage.distance.EuclidDistance;
import com.coverage.distance.IDistance;
import com.coverage.main.KM;
import com.coverage.main.show.Show;
import com.coverage.models.Point;
import com.coverage.models.Relay;
import com.coverage.models.Sensor;
import com.coverage.models.Target;

public class HeuristicSolveKCoverage implements Algorithms{
	private IDistance distance = 
			new EuclidDistance();		// function distance
	
	private SensorGeneration sensorGeneration = new SensorGeneration();
	private Set<Target> targets;
	
	public HeuristicSolveKCoverage(Set<Target> targets) {
		this.targets = targets;
	}
	
	/**
	 * Run algorithms
	 */
	@Override
    public void run(List<Sensor> resultSensors, List<Relay> resultRelays, List<Integer> listResults){
		Integer numOfSensors;
		Integer numOfRelays;
		
    	// phase 1, result of phase1 is list of sensors was satisfy k coverage
        Set<Sensor> sensors = 
        		//buildSensor(targets);
				buildSensor2(targets); // improve build sensor (phase1, step1)
		
        HashMap<Sensor, Integer> mapWS = weightOfSensor(sensors, targets);
        LinkedHashMap<Sensor, Integer> sortedMap = sortHashMapByValue(mapWS);
        sensors = optimalSensor(sortedMap, targets);
        
//       // check set sensor is coverage
//        for(Target t: targets){
//			int w=0;
//			for(Sensor s : sensors){
//				if(t.isCoverage(s)) w++;
//			}
//			System.out.println(w);
//		}
        
        List<Sensor> results = new ArrayList<Sensor>(sensors);		// convert from set to list -end phase 1-
        // add result sensors 
        resultSensors.addAll(results);
        numOfSensors = resultSensors.size();
        
        // show result phase1 : list of sensors coverage target
        System.out.println("List of sensor satisfy k coverage is : ");
        Show.printList(results);
        
        // phase 2, result of phase2 is list of relays
        Map<Integer, List<Integer>> path = findMinPathToBase(results);
        Set<Integer> keySets = path.keySet();
        
//        System.out.println("Path of each sensor to base satisfy m connectivity : \n");
//        for(int key : keySets) {
//        	List<Integer> list = path.get(key);
//        	list.forEach(l -> System.out.print(" -> " + l));
//        	
////        	System.out.println(" : " + costOnePath(list, results, distance));
//        	System.out.println();
//        }
//        System.out.println("Cost : " + cost(path, results));
//        System.out.println("\n-----------------\n");
        
        System.out.println("Path of each sensor to base satisfy two sensor coverage a same target not same path : \n");
        path = checkConstraintConnectivity(path, results);
        keySets = path.keySet();
        
        for(int key : keySets) {
        	List<Integer> list = path.get(key);
        	list.forEach(l -> System.out.print(" -> " + l));
//        	System.out.println(" : " + costOnePath(list, results, distance));
        	System.out.println();        	
        }
        
        // add result relays
        // resultRelays.addAll(results); 
        numOfRelays = countRelays(path, results);
        
        // add number of sensors and relays
        listResults.add(numOfSensors);
        listResults.add(numOfRelays);
    }
	
	/**
	 * the method calculate cost for relays and sensors
	 */
	public int countRelays(Map<Integer, List<Integer>> path, List<Sensor> listSensors) {
		int numberOfRelays = 0;
		Map<Integer, Integer> mapPath = new HashMap<Integer, Integer>();
		
		Set<Integer> keySets = path.keySet();
		for(int key : keySets) {
			List<Integer> list = path.get(key); // list index of sensors
			
			for(int i=0; i<list.size()-1; i++) {
				int node_1 = list.get(i);
				int node_2 = list.get(i+1);
				
				mapPath.put(node_1, node_2);
			}
			
		}
		
		double total = 0;
		keySets = mapPath.keySet();
		for(int key : keySets) {
			if(key == listSensors.size()) {
				total += distance.caculate(KM.BASE, listSensors.get(mapPath.get(key)));
			} else if(mapPath.get(key) == listSensors.size()) {
				total += distance.caculate(listSensors.get(key), KM.BASE);
			} else {
				total += distance.caculate(listSensors.get(key), listSensors.get(mapPath.get(key)));
			}
		}
		
		numberOfRelays =(int) ( total / KM.RC + 0.5);
		
//		System.out.println("\nNumber of relays : " + numberOfRelays);
//		return numberOfRelays * KM.ANPHA + listSensors.size() * KM.BETA;
		
		return numberOfRelays;
	}
	
	// bonus function, calculate cost of each path from sensor to base
	public double costOnePath(List<Integer> list, List<Sensor> listSenSors, IDistance distance) {
		double total = 0;
		for(int i=0; i<list.size()-1; i++) {
			int node_1 = list.get(i);
			int node_2 = list.get(i+1);
			
			if(node_1 >= listSenSors.size()) { // is base
				total += distance.caculate(KM.BASE, listSenSors.get(node_2));
			} else if(node_2 >= listSenSors.size()) {
				total += distance.caculate(KM.BASE, listSenSors.get(node_1));
			} else {
				total += distance.caculate(listSenSors.get(node_1), listSenSors.get(node_2));
			}
			
		}
		
		return total;
	}
	
	/**
	 * Phase 1, Step 1 :
	 * Build list sensor with k coverage (no optimal)
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
	 */
    public Set<Sensor> optimalSensor(LinkedHashMap<Sensor, Integer> sensors, Set<Target> targets){
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
     * Phase 2 : 
     * Build full graph from list sensors satisfy k coverage optimal and base station
     * calculate minimum path from each sensor to base
     * and return list sensor on the path from each sensor to base
     */
    public Map<Integer, List<Integer>> findMinPathToBase(List<Sensor> listSensors) {
    	List<Point> listPoints = new ArrayList<Point>(listSensors);
    	listPoints.add(KM.BASE);    	
    	
    	Map<Integer, Integer> trace = new Dijkstra(listPoints).trace;
    	int base_id = listPoints.size()-1;
    	
//    	Set<Integer> keySets = trace.keySet();
//    	for(int key : keySets) {
//    		System.out.println(key + " " + trace.get(key));
//    	}
//    	System.out.println("\n-----------------\n");
    	
    	Map<Integer, List<Integer>> path = new HashMap<Integer, List<Integer>>();
     	for(int i=0; i<listPoints.size()-1; i++) {
     		int now = i;		
    		List<Integer> list = new ArrayList<Integer>();
    		list.add(now);
    		while(now != base_id) {
    			now = trace.get(now);
    			list.add(now);
    		}
    		
    		path.put(i, list);
    	}
    	
    	return path;
    }
    
    /**
     * Method check if two sensor coverage same target, see they cann't same path
     */
    public Map<Integer, List<Integer>> checkConstraintConnectivity(Map<Integer, List<Integer>> path, List<Sensor> sensors) {
    	int lens = sensors.size();
    	for(int i=1; i<lens; i++) {
//    		for(int j=0; j<lens; j++) {
//    			if(sensors.get(i).sameCoverage(sensors.get(j))) {	// coverage same target
//    				List<Integer> list_i = path.get(i);
//    				List<Integer> list_j = path.get(j);
//    				
//    				List<Integer> inters = 
//    						list_i.stream().distinct().filter(list_j::contains).collect(Collectors.toList());
//    				
//    				if(inters.size() > 1) {
//    					return false;
//    				}
//    			}
//    		}
    		
    		List<Integer> listCannotInPath = new ArrayList<Integer>(); // list point cann't in this path of node i
    		for(int j=0; j<i; j++) {
    			if(sensors.get(i).sameCoverage(sensors.get(j))) {
    				listCannotInPath.addAll(path.get(j).subList(1, path.get(j).size()-1)); 
    			}
    		}
    		
    		for(int id=1; id<path.get(i).size()-1; id++) {
    			if(listCannotInPath.contains(path.get(i).get(id))) {
    				int n_id = 0;
    				
//    				Random rand = new Random();
//    				n_id = rand.nextInt(lens);
//    				
//    				while(listCannotInPath.contains(n_id) 
//    						|| !sensors.get(n_id).sameCoverage(sensors.get(path.get(i).get(id)))) {
//    					n_id = rand.nextInt(lens);
//    					
//    				}
    				
    				for(int r=0; r<lens; r++) {
    					n_id = r;
    					if(!listCannotInPath.contains(n_id) && sensors.get(n_id).sameCoverage(sensors.get(path.get(i).get(id)))) {
    						break;
    					}
    				}
    				
    				path.get(i).set(id, n_id);
    			}
    		}
    	}
    	
    	return path;
    }
    
    /**
     * hash two integer
     */
    public int hash(int a, int b) {
    	return Hash.hashCode(a, b);
    }
    
	/**
	 * The method return number of intersection of two circle
	 * for phase 1
	 */
	public List<Sensor> intersection(Target t1, Target t2) {
		List<Sensor> sensors = new ArrayList<>();
		if (distance.caculate(t1, t2) < 2 * KM.RS) {
			if(t1.getX()==t2.getX()){
				double tmp1=t1.getX();
				double tmp2=t1.getY();
				double tmp3=t2.getY();
				t1.setX(tmp2);
				t1.setY(tmp1);
				t2.setX(tmp3);
				t2.setY(tmp1);
				double a = EquationCircle.solveA(t1.getX(), t1.getY(), t2.getX(), t2.getY());
				double b = EquationCircle.solveB(t1.getX(), t1.getY(), t2.getX(), t2.getY());
				double c = EquationCircle.solveC(t1.getX(), t1.getY(), t2.getX(), t2.getY(), KM.RS);
				List<Double> listY = EquationCircle.solveQuadraticEquations(a, b, c);

				for (double y : listY) {
					double x = EquationCircle.sloveX(y, t1.getX(), t1.getY(), t2.getX(), t2.getY());
					sensors.add(new Sensor(y, x));
				}
				t1.setX(tmp1);
				t1.setY(tmp2);
				t2.setX(tmp1);
				t2.setY(tmp3);
				return sensors;
			}
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
	 * for phase 1
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

	/**
	 * New phase1
	 */
	public Set<Sensor> generateSensor1(Set<Target> targets) {
		Set<Sensor> sennors = new HashSet<>();
		List<Target> targetList = new ArrayList<>();
		targetList.addAll(targets);
		for (int i = 0; i < targetList.size() - 1; i++) {
			for (int j = i + 1; j < targetList.size(); j++) {
				List<Sensor> listSensor = intersection(targetList.get(i), targetList.get(j));
				if (listSensor!=null) {
					sennors.addAll(listSensor);
				}
			}
		}

		for(Target t : targets){
			while(true){
				Sensor s = sensorGeneration.compute(t);
				if(!sennors.contains(s)){
					sennors.add(s);
					break;
				}
			}
		}

		return sennors;
	}

	public LinkedHashMap<Sensor, List<Target>> generateMap(Set<Sensor> sensors){
		List<Target> targets=new ArrayList<>();
		targets.addAll(this.targets);
		LinkedHashMap<Sensor, List<Target>> map = new LinkedHashMap<>();
		for(Sensor sensor: sensors){
			List<Target> targetList = new ArrayList<>();
			for(Target target : targets){
				if(target.isCoverage(sensor)){
					targetList.add(target);
				}
			}
			map.put(sensor, targetList);
		}
		return map;
	}

	public LinkedHashMap<Sensor, List<Target>> sortMap(LinkedHashMap<Sensor, List<Target>> hashMap){
		Set<Map.Entry<Sensor, List<Target>>> entries = hashMap.entrySet();

		Comparator<Map.Entry<Sensor, List<Target>>> comparator = new Comparator<Map.Entry<Sensor, List<Target>>>() {
			@Override
			public int compare(Map.Entry<Sensor, List<Target>> e1, Map.Entry<Sensor, List<Target>> e2) {
				int v1 = e1.getValue().size();
				int v2 = e2.getValue().size();
				if(v1==v2) return 0;
				else if( v1>v2) return -1;
				else return 1;
			}
		};

		List<Map.Entry<Sensor, List<Target>>> listEntries = new ArrayList<>(entries);
		Collections.sort(listEntries, comparator);
		LinkedHashMap<Sensor, List<Target>> sortedMap = new LinkedHashMap<>(listEntries.size());
		for (Map.Entry<Sensor, List<Target>> entry : listEntries) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	public boolean isMidPoint(Sensor s, Target t1, Target t2){
		if(distance.caculate(s, t1)+distance.caculate(s, t2)==distance.caculate(t1, t2)) return true;
		return false;
	}


	public Set<Sensor> generateKSensor(Sensor s, List<Target> list){
		Set<Sensor> sensors = new HashSet<>();
		Set<Sensor> giao = new HashSet<>();
		Sensor tmps = null;
		if(list.size()>=2){
			sensors.add(s);
			for(int i=0;i<list.size()-1;i++){
				for(int j=i+1;j<list.size();j++){
					List<Sensor> listSensor = intersection(list.get(i), list.get(j));
					if (listSensor!=null) {
						giao.addAll(listSensor);
					}
				}
			}
			for(Sensor s1: giao){
				boolean b = true;
				for(Target t : list){
					if(!t.isCoverage(s1)){
						b=false;
					}
				}
				if(s1.getX()==s.getX()&& s1.getY()==s.getY()) b=false;
				if (b){
					tmps=s1;
					sensors.add(s1);
				}
			}
			double x0=tmps.getX()-s.getX();
			double y0=tmps.getY()-s.getY();
			Random rd = new Random();
			while (sensors.size()<KM.K) {
				double nrd = rd.nextDouble();
				double x = s.getX() + x0 * nrd;
				double y = s.getY() + y0 * nrd;
				sensors.add(new Sensor(x,y));
			}
		}else {
			sensors.add(s);
			Target t= new Target(list.get(0).getX(), list.get(0).getY());
			while(sensors.size()<KM.K){
				sensors.add(sensorGeneration.compute(t));
			}
		}
		return sensors;
	}

	public Set<Sensor> generateSensor2(LinkedHashMap<Sensor, List<Target>> map){
		Set<Sensor> sensors = new HashSet<>();
		Sensor removeSensor=null;
		List<Target> removeTarget = new ArrayList<>();
		boolean b;
		while (map.size()>0){
			map=sortMap(map);
			for (Map.Entry<Sensor, List<Target>> entry : map.entrySet()) {
				Sensor s = entry.getKey();
				removeSensor = new Sensor(s.getX(), s.getY());
				sensors.add(new Sensor(s.getX(), s.getY()));
				List<Target> list = entry.getValue();
				removeTarget.addAll(list);
				if(list.size()>=2){
					b = false;
					for(int i =0; i<list.size()-1;i++){
						for(int j=0;j<list.size() ;j++){
							if(isMidPoint(s, list.get(i), list.get(j))){
								b=true;
							}
						}
					}
					if(b==false){
						sensors.addAll(generateKSensor(s,list));
					}
					else {
						removeTarget.clear();
					}
				}else {
					sensors.addAll(generateKSensor(s, list));
				}
				break;
			}
			map.remove(removeSensor);
			Set<Sensor> r = new HashSet<>();
			for(Target t1 : removeTarget){
				for (Map.Entry<Sensor, List<Target>> entry : map.entrySet()) {
					List<Target> t2= entry.getValue();
					t2.remove(t1);
					if(t2.size()==0){
						r.add(entry.getKey());
					}
					map.put(entry.getKey(), t2);
				}
			}
			for (Sensor s : r){
				map.remove(s);
			}
		}
		return sensors;
	}

	public Set<Sensor> buildSensor2(Set<Target> targets){
		// sinh các điểm mấu chốt
		Set<Sensor> sensors = generateSensor1(targets);
		// tạo map các key là sensor value là list Target nó bảo phủ
		LinkedHashMap<Sensor, List<Target>> mapSLT=generateMap(sensors);
		// sinh ra đủ K sensor bao phủ các target đó
		sensors = generateSensor2(mapSLT);
		return sensors;
	}
}
