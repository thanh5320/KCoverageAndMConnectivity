package com.coverage.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

public class Demo {
    public static void main(String[] args) throws IOException {
//    	expe1();
    	expe2();
    	
    }
    
    // change K, with target = 100
    public static void expe2() throws IOException {
    	CSVWriter csv = new CSVWriter(new FileWriter(new File("results/K_expe/1_algo.csv"), true));
    	KM km = new KM("resources/target_100.txt", 1);
		km.run();
		
		int numOfSensors = km.getNumOfSensors();
		int numOfRelays = km.getNumOfRelays();
		double cost = km.getCost();
		
		String[] row = new String[4];
		row[0] = Integer.toString(KM.K);
		row[1] = Integer.toString(numOfSensors);
		row[2] = Integer.toString(numOfRelays);
		row[3] = Double.toString(cost);
		
		csv.writeNext(row);
    	
    	csv.close();
    }
    
    // change target
    public static void expe1() throws IOException {
    	List<String> filenames = new ArrayList<String>();
    	for(int i=50; i<300; i=i+50) {
    		String filename = "resources/target_" + i + ".txt";
    		filenames.add(filename);
    	}
    	    	
    	CSVWriter csv = new CSVWriter(new FileWriter(new File("results/target_expe/1_algo.csv")));
    	int numTarget = 50;
    	for(String filename: filenames) {
        	KM km = new KM(filename, 1);
    		km.run();
    		
    		int numOfSensors = km.getNumOfSensors();
    		int numOfRelays = km.getNumOfRelays();
    		double cost = km.getCost();
    		
    		String[] row = new String[4];
    		row[0] = Integer.toString(numTarget);
    		row[1] = Integer.toString(numOfSensors);
    		row[2] = Integer.toString(numOfRelays);
    		row[3] = Double.toString(cost);
    		
    		csv.writeNext(row);
        	
        	numTarget += 50;
    	}
    	
    	csv.close();
    }
}
