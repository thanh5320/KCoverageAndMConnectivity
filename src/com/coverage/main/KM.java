package com.coverage.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import com.coverage.algorithm.Algorithms;
import com.coverage.algorithm.heuristic.HeuristicSolveKCoverage;
import com.coverage.models.Relay;
import com.coverage.models.Sensor;
import com.coverage.models.Target;

public class KM {
    // problem parameters
    public static final double RC = 20; // connection transmission radius
    public static final double RS = 5; // coverage radius
    public static final int K = 3;
    
	public static final String url = "resources/target.txt";
    
	private Set<Sensor> sensors;
    private Set<Relay> relays;
    
    public KM() {
    	sensors = new HashSet<Sensor>();
    	relays = new HashSet<Relay>();
    }
    
    /**
     * Read input target
     * @param url
     * @return
     */
    public static Set<Target> readFileTarget(){
        Set<Target> targets = new HashSet<>();
        File file = new File(url);
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
                targets.add(new Target(Integer.parseInt(s[0]), Integer.parseInt(s[1])));
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
    	Set<Target> targets = readFileTarget();
    	Algorithms algorithms = 
    			new HeuristicSolveKCoverage(targets);
    	
       algorithms.run(sensors, relays);
    }

    // getter and 
    // get result after perform algorithms
    
	public Set<Sensor> getSensors() {
		return sensors;
	}
	
	public Set<Relay> getRelays() {
		return relays;
	}
    
}
