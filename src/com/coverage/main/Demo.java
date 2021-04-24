package com.coverage.main;

import com.coverage.main.show.Show;

public class Demo {
    public static void main(String[] args) {
        KM km = new KM();
        km.run();
        
        Show.print(km.getSensors());
        Show.print(km.getRelays());
    }
}
