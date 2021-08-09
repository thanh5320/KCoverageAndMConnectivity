package com.coverage.algorithm.ga;

import com.coverage.algorithm.ga.hybrid.HybridInterface;
import com.coverage.algorithm.Algorithms;
import com.coverage.algorithm.ga.hybrid.HybridCrossoverPoint;
import com.coverage.algorithm.ga.mutation.BitInversionMutation;
import com.coverage.algorithm.ga.mutation.MutationInterface;
import com.coverage.algorithm.support.Dijkstra;
import com.coverage.distance.EuclidDistance;
import com.coverage.distance.IDistance;
import com.coverage.main.KM;
import com.coverage.main.KMGA1;
import com.coverage.main.show.Show;
import com.coverage.models.Point;
import com.coverage.models.Relay;
import com.coverage.models.Sensor;
import com.coverage.models.Target;

import java.util.*;

public class GA1Algorithms implements Algorithms{
	private IDistance distance = 
			new EuclidDistance();		// function distance
	
    private HybridInterface hybird = new HybridCrossoverPoint();
    private MutationInterface mutation = new BitInversionMutation();
    int repeatLimit = 200;
    
    // corner points of the domain
    private Point topLeftPoint;
    private Point topRightPoint;
    private Point bottomLeftPoint;
    private Point bottomRightPoint;

    // distance between potential points
    private double potentialPointDistanceX=(1.0*KMGA1.RS) / KMGA1.K+10;
    private double potentialPointDistanceY=(1.0*KMGA1.RS) / KMGA1.K+10;
    
    // potential point
    private List<Sensor> potentialPoints;
    
    // current generation
    private List<int[]> chromosomes;// 1 is sensor, 0 isn't sensor

    // number of individual
    private int numberOfChromosomes = 50;

    // ratio of father, mother and child
    private double percentOfParents=0.5;
    private double percentOfChildren=0.5;
    private double percentOfmutation = 0.2;

    // weight of F1, F2 in fitness
    private double w1=0.5;
    private  double w2=0.5;

    private Set<Target> targets;

    // current fitness
    Map<int[], Double> fitness;

    public GA1Algorithms(Set<Target> targets) {
        this.targets = targets;
        this.chromosomes = new ArrayList<>();
        potentialPoints = new ArrayList<>();
        
        potentialPointBorder();
        generatePotentialPoint();
        generateChromosomes();

        this.fitness = new HashMap<>();
        for(int[] s: chromosomes){
            fitness.put(s, fitness(s));
        }
    }

    // find domain
    public void potentialPointBorder(){
        double minX=Double.MAX_VALUE;
        double minY=Double.MAX_VALUE;
        double maxX=Double.MIN_VALUE;
        double maxY=Double.MIN_VALUE;
        
        for(Target target : targets){
            if(minX>target.getX()) minX=target.getX();
            if(minY>target.getY()) minY=target.getY();
            if(maxX<target.getX()) maxX=target.getX();
            if(maxY<target.getY()) maxY=target.getY();
        }
        
        // domain will minX, minY, maxX, maxY widen a distance
        this.bottomLeftPoint=new Point(minX-KMGA1.RS, minY-KMGA1.RS);
        this.bottomRightPoint=new Point(maxX+KMGA1.RS, minY-KMGA1.RS);
        this.topLeftPoint=new Point(minX-KMGA1.RS, maxY+KMGA1.RS);
        this.topRightPoint=new Point(maxX+KMGA1.RS, maxY+KMGA1.RS);
    }

    // create potential point
    public void generatePotentialPoint(){
        double x;
        double y;
        
        double maxX = topRightPoint.getX();
        double maxY = topRightPoint.getY();

        x = bottomLeftPoint.getX();;
        while (x<=maxX){
            y = bottomLeftPoint.getY();
            while(y<=maxY){
                Sensor p = new Sensor(x, y);
                y += potentialPointDistanceY;
                this.potentialPoints.add(p);
            }
            x+=potentialPointDistanceX;
        }
        
        this.potentialPoints.forEach(p -> System.out.println(p.getX() + " " + p.getY()));
    }

    // initialize population
    public void generateChromosomes(){
        int len = this.potentialPoints.size();
        for(int i=0;i<numberOfChromosomes;i++){
            int[] chromosome = new int[len];
            for(int j=0;j<len;j++){
                if(Math.random()<0.5) chromosome[j]=0;
                else chromosome[j]=1;
            }
            chromosomes.add(chromosome);
        }
    }

    // create new population
    public void generateNewPopulation(){
        List<int[]> rs = new ArrayList<>();
        List<int[]> newChromosomes = new ArrayList<>();

        // hybrid
        for(int i=0;i<chromosomes.size()-1;i=i+2){
            List<int[]> child = hybird.generateChildren(chromosomes.get(i), chromosomes.get(i+1), potentialPoints.size());
            rs.addAll(child);
        }

        // mutation
        Random random = new Random();
        double nrd = random.nextDouble();
        for(int i=0;i<numberOfChromosomes;i++){
            if(nrd<percentOfmutation) rs.set(i,mutation.mutate(rs.get(i), potentialPoints.size()));
        }

        Map<int[], Double> mapChildren = new HashMap<>();
        for(int[] s: rs){
            mapChildren.put(s, fitness(s));
        }
        LinkedHashMap<int[], Double> sortedMapParent = sortMapByValue(fitness);
        LinkedHashMap<int[], Double> sortedMapChildren = sortMapByValue(mapChildren);
        int c = 0;
        Set<Map.Entry<int[], Double>> sortedEntries = sortedMapParent.entrySet();
        for (Map.Entry<int[], Double> mapping : sortedEntries) {
            c++;
            if(c>percentOfParents*numberOfChromosomes) break;
            newChromosomes.add(mapping.getKey());
        }
        c--;

        sortedEntries = sortedMapChildren.entrySet();
        for (Map.Entry<int[], Double> mapping : sortedEntries) {
            c++;
            if(c>numberOfChromosomes) break;
            newChromosomes.add(mapping.getKey());
        }
        chromosomes = newChromosomes;
        fitness = mapChildren;
    }

    // calculator fitness of individual
    public double fitness(int[] individual){
    	// Objective 1
        int p=potentialPoints.size(); // all potential point
        int m = 0; // number of sensor, is potential point with value is 1
        for(int i=0;i<p;i++){
            if(individual[i]==1) m++;
        }
        
        // F1, objective minimize F1
        double f1 = (m*1.0)/p;
        
        // Objective 2
        int sumCovCost = 0;
        for(Target target : targets){
            int c = 0;
            for(int i=0;i<p;i++){
                if(individual[i]==1 && target.isCoverage(potentialPoints.get(i))) c++;
            }
            if(c>=KMGA1.K) sumCovCost +=KMGA1.K;
            else sumCovCost+=KMGA1.K-c;
        }

        // F2, objective maximize F2
        double f2 = (1.0/(targets.size()*KMGA1.K))*sumCovCost;
        
        // Fitness = W1 * (1 - F1) + W2 * F2
        double fitness = w1*(1.0-f1) + w2*f2;
        
        return fitness;
    }

    // Sort individual in population with fitness
    public LinkedHashMap<int[], Double> sortMapByValue(Map<int[], Double> map){

        // create set entry
        Set<Map.Entry<int[], Double>> entries = map.entrySet();

        // create custom Comparator
        Comparator<Map.Entry<int[], Double>> comparator = new Comparator<Map.Entry<int[], Double>>() {
            @Override
            public int compare(Map.Entry<int[], Double> e1, Map.Entry<int[], Double> e2) {
                double v1 = e1.getValue();
                double v2 = e2.getValue();
                
                if(v1==v2) return 0;
                else if(v1>v2) return 1;
                else return -1;
            }
        };

        // convert Set to List
        List<Map.Entry<int[], Double>> listEntries = new ArrayList<>(entries);

        // sort List
        Collections.sort(listEntries, comparator);

        // create a LinkedHashMap and put entries from List sorted to
        LinkedHashMap<int[], Double> sortedMap = new LinkedHashMap<>(listEntries.size());
        for (Map.Entry<int[], Double> entry : listEntries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    // Run algorithms with potential point fixed
//    public void run(){
//        int i = repeatLimit;
//        System.out.println("ban đầu:");
//
//        Set<int[]> set = fitness.keySet();
//        for (int[] key : set) {
//            System.out.println(key + " " + fitness.get(key));
//        }
//
//        while (i-->0){
//            generateNewPopulation();
//            System.out.println("lần: " +i);
//
//            Set<int[]> set1 = fitness.keySet();
//            for (int[] key : set1) {
//                System.out.println(key + " " + fitness.get(key));
//            }
//        }
//    }

    // increase a number of potential until satisfy k-coverage
    private int[] rs;
    public void runKCoverage(){
    	rs = null; // chromosome result  
    	
    	potentialPointDistanceX=KMGA1.RS;
        potentialPointDistanceY=KMGA1.RS;
        
        int nosMin = Integer.MAX_VALUE;
        
        Boolean b = false;
        int dem=1;
        
        while (!b){
            System.out.println("lần lặp thứ: " + dem++);
            resetValue();
            int i = repeatLimit;
            while (i-->0){
                generateNewPopulation();
            }

            for(int[] chromosome : chromosomes){
                boolean b2=true;
                for(Target target : targets){
                    int c=0;
                    for(int j=0;j<potentialPoints.size();j++){
                        if(chromosome[j]==1 && target.isCoverage(potentialPoints.get(j))){ c++;}
                    }
                    if(c<=KMGA1.K){ b2 =false; }
                }
                if(b2==true){
                    b=true;
                    int nos=0;
                    for(int j=0;j<potentialPoints.size();j++){
                        if(chromosome[j]==1)
                            nos++;
                    }
                    if(nos<nosMin) {nosMin = nos; rs=chromosome;}
                }
            }
            
            potentialPointDistanceX-=10.0/dem;
            potentialPointDistanceY-=10.0/dem;
        }
        
//        System.out.println(nosMin);
    }

    // reset value when initialize object
    public void resetValue(){
        this.chromosomes = new ArrayList<>();
        potentialPoints = new ArrayList<>();
        
        generatePotentialPoint();
        generateChromosomes();

        this.fitness = new HashMap<>();
        for(int[] s: chromosomes){
            fitness.put(s, fitness(s));
        }
    }

	@Override
	public void run(List<Sensor> resultSensors, List<Relay> resultRelays, List<Integer> listResults) {
		// Phase 1: GA Algorithms
		this.runKCoverage();
		
		for(int i=0; i<rs.length; i++) {
			if(rs[i] == 1) {
				resultSensors.add(potentialPoints.get(i));
			}
		}
		
		listResults.add(resultSensors.size()); // add number of sensors to location 0 in list 
		
		// show result phase1 : list of sensors coverage target
        System.out.println("List of sensor satisfy k coverage is : ");
        Show.printList(resultSensors);
        
		// Phase 2: same with phase 2 in heuristic algorithms, Dijkstra
		Map<Integer, List<Integer>> path = findMinPathToBase(resultSensors);
        Set<Integer> keySets = path.keySet();
		
        System.out.println("Path of each sensor to base satisfy two sensor coverage a same target not same path : \n");
        path = checkConstraintConnectivity(path, resultSensors);
        keySets = path.keySet();
        
        for(int key : keySets) {
        	List<Integer> list = path.get(key);
        	list.forEach(l -> System.out.print(" -> " + l));
//        	System.out.println(" : " + costOnePath(list, results, distance));
        	System.out.println();        	
        }
        
        // add result relays
        // resultRelays.addAll(results); 
        int numOfRelays = countRelays(path, resultSensors);
        
        listResults.add(numOfRelays); // add number of relays to location 1 in list 
	}
	
	/**
	 * Phase2: Dijkstra same with heuristic algorithms with phase 2
	 */
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
}
