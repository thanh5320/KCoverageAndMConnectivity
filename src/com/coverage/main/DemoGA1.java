package com.coverage.main;

import com.coverage.algorithm.ga.GA1;

public class DemoGA1 {
    public static void main(String[] args) {
        GA1 ga1 = new GA1(KMGA1.readFileTarget());
        ga1.runKConnect();
        //ga1.run();
    }
}
