package com.coverage.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.coverage.algorithm.Algorithms;
import com.coverage.algorithm.ga.GA1Algorithms;
import com.coverage.algorithm.heuristic.HeuristicAlgorithms;
import com.coverage.models.Base;
import com.coverage.models.Relay;
import com.coverage.models.Sensor;
import com.coverage.models.Target;

public class KM {
    // problem parameters
    public static double RS = 40; 				// coverage radius
    public static double RC = RS * 2; 				// connection transmission radius
    public static int K = 8; 					// constraint coverage
    public static Base BASE = new Base(5, 5); // base station
    public static Set<Target> TARGETS; 			// list input target
    public static double ANPHA = 0.4; 			// weigh of relay
    public static double BETA = 0.6; 			// weigh of sensor
    
	private String url = "resources/target.txt";
	private int nAlgorithm;
    
	private List<Sensor> sensors;
    private List<Relay> relays;
    
    private Integer numOfSensors;
    private Integer numOfRelays;
    private Double cost;
    
    public KM(String url, int nAlgorithm) {
    	this.url = url;
    	this.nAlgorithm = nAlgorithm;
    	
    	sensors = new ArrayList<Sensor>();
    	relays = new ArrayList<Relay>();
    	TARGETS = readFileTarget();
    }
    
    /**
     * Read input target
     */
    public Set<Target> readFileTarget(){
        Set<Target> targets = new HashSet<>();
        File file = new File(this.url);
        BufferedReader reader=null;
        try {
             reader= new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            System.out.println("Error read file input !");
            e.printStackTrace();
        }
        try {
            String line = reader.readLine();
            while(line!=null){
                String[] s = line.split(" ");
                targets.add(new Target(Double.parseDouble(s[0]), Double.parseDouble(s[1])));
                line=reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return targets;
    }
    
    /**
     * Main function, run and find sensors and relays with k coverage and m connectivity
     */
    public void run() {
    	Algorithms algorithms = null;
    	if(this.nAlgorithm == 1) {
    		algorithms = new HeuristicAlgorithms(TARGETS, false); // heuristic 
    	} else if(this.nAlgorithm == 2) {
    		algorithms = new HeuristicAlgorithms(TARGETS, true); // heuristic improve
    	} else {
    		algorithms = new GA1Algorithms(TARGETS); // GA
    	}
    	
    	List<Integer> listResults = new ArrayList<Integer>(); 
    	algorithms.run(sensors, relays, listResults);
    	
    	// some algorithms, i donn't need list of relays, we only need number of relays
    	// so we need parameter get number of sensors and relays
    	numOfSensors = listResults.get(0);
    	numOfRelays = listResults.get(1);
    	
    	System.out.println("\nNumber of Sensor : " + numOfSensors);
    	System.out.println("Number of Relays : " + numOfRelays);
    	
    	cost = numOfRelays * KM.ANPHA + numOfSensors * KM.BETA;
        System.out.println("Cost : " + cost);
       
    }

    // getter and 
    // get result after perform algorithms
    
	public List<Sensor> getSensors() {
		return sensors;
	}
	
	public List<Relay> getRelays() {
		return relays;
	}

	public Integer getNumOfSensors() {
		return numOfSensors;
	}
	
	public Integer getNumOfRelays() {
		return numOfRelays;
	} 
	
	public Double getCost() {
		return cost;
	}
}
