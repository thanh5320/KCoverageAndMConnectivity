package com.coverage.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVWriter;

public class Demo {
    public static void main(String[] args) throws IOException {
//    	CSVWriter csv = new CSVWriter(new FileWriter(new File("results/3_5_i.csv")));
//    	
//    	for(int i=0; i<30; i++) {
//    		KM km = new KM();
//    		km.run();
//    		
//    		int numOfSensors = km.getNumOfSensors();
//    		int numOfRelays = km.getNumOfRelays();
//    		double cost = km.getCost();
//    		
//    		String[] row = new String[3];
//    		row[0] = Integer.toString(numOfSensors);
//    		row[1] = Integer.toString(numOfRelays);
//    		row[2] = Double.toString(cost);
//    		
//    		csv.writeNext(row);
//     	}
//    	
//    	csv.close();
    	
    	KM km = new KM();
    	km.run();
    	
    	
    }
}
