package com.coverage.algorithm.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.coverage.distance.EuclidDistance;
import com.coverage.distance.IDistance;
import com.coverage.main.KM;
import com.coverage.models.Point;
import com.coverage.models.Target;

/**
 * Class build fully graph with list point input, 
 * calculate and return distance minimum between each point with base station
 *
 */
public class Dijkstra {
	private Map<Integer, List<Integer>> adj; 	// adjacency list graph
	List<Point> listPoints; 					// list point in graph
	public Map<Integer, Integer> trace; 		// previous node 
	private boolean[] mark;						// mark point in priority queue
	private double[] dist;						// distance between point i with base station
	
	public Dijkstra(List<Point> listPoints) {
		trace = new HashMap<Integer, Integer>();
		adj = new HashMap<Integer, List<Integer>>();
		this.listPoints = listPoints;

		int base_id = listPoints.size() - 1; 
		
		// initialization adjacency
		// build fully graph with list point, each edge adjacent with all edge other
		// so that, two point no coverage same target (function checkSensorGroup will check this constraint) 
		for(int i=0; i<listPoints.size(); i++) {
    		List<Integer> list = new ArrayList<Integer>();
//    		if(i==0) {
//    			for(int j=0; j<listPoints.size(); j++) {
//    				if(j != i && j != base_id) {
//    					list.add(j);
//    				}
//    			}
//    			
//    			adj.put(i, list);
//    			continue;
//    		}
//    		
    		if(i == base_id) { // if point i is base, add all other node to adjacency of node i
    			for(int j=0; j<listPoints.size(); j++) {
    				if(j != i /*&& j != 0*/) {
    					list.add(j);
    				}
    			}
    		} else {
    			for(int j=0; j<listPoints.size(); j++) {
    				if(j == base_id) { // base adjacency all other node in graph
    					list.add(j);
    				} else { 
    					if(j != i && !checkSensorGroup(listPoints.get(i), listPoints.get(j))) {
            				list.add(j);
            			}
    				}
        		}
    		}
    		
    		adj.put(i, list);
    	}
		
		dist = new double[listPoints.size()]; 			// dist[u] = distance(u, base)
		mark = new boolean[listPoints.size()]; 			// mark point in priority queue, mark[i] = true => point i in priority queue
		for(int i=0; i<listPoints.size(); i++) {
			dist[i] = Double.MAX_VALUE; 				// initialization
			mark[i] = true; 							// make queue, all point initialization in priority queue except base
		}
		dist[base_id] = 0;
		
		// Dijkstra algorithms
		while(!isEmpty()) {
			int u = deletemin();
			List<Integer> listEdge = adj.get(u);
			for(int edge : listEdge) {
				if(dist[edge] > dist[u] + l_distance(u, edge)) {
					dist[edge] = dist[u] + l_distance(u, edge);
					trace.put(edge, u);
				}
			}
		}

	}
	
	/* method same with check empty for priority queue H
	 * */
	public boolean isEmpty() {
		int cnt = 0;
		for(int i=0; i<listPoints.size(); i++) {
			if(mark[i]) {
				cnt++;
			}
		}
		
		if(cnt == 0) return true;		// queue is empty
		return false;
	}

	/* method same with delete min of priority queue H
	 * */
	public int deletemin() {
		int lens = listPoints.size();
		int min_index = -1;
		double min_value = Double.MAX_VALUE;
		for(int i=0; i<lens; i++) {
			if(mark[i] == true) { // node i in priority queue
				if(dist[i] <= min_value) {
					min_index = i;
					min_value = dist[i];
				}
			}
		}
		
		mark[min_index] = false;
		return min_index;
	}
	
	/* Calculate the distance between 2 vertices of the graph
	 * */
	public double l_distance(int a, int b) {
		IDistance distance = new EuclidDistance();
		
		return distance.caculate(listPoints.get(a), listPoints.get(b));
	}
	
	
    /*
     * The method return true if sensor1 and sensor2 coverage the same target
     * and false if no
     * */
    public boolean checkSensorGroup(Point sensor1, Point sensor2) {
    	for(Target t : KM.TARGETS) {
    		if(t.isCoverage(sensor1) && t.isCoverage(sensor2)) {
    			return true;
    		}
    	}
    	
    	return false;
    }
}
