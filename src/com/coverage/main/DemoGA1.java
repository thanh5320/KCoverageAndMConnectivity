package com.coverage.main;

import com.coverage.algorithm.ga.GA1Algorithms;

public class DemoGA1 {
    public static void main(String[] args) {
        GA1Algorithms ga1 = new GA1Algorithms(KMGA1.readFileTarget());
        ga1.runKCoverage();
        //ga1.run();
    }
}
