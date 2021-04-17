package main;

import algorithm.KCoverage;
import model.Sensor;
import model.Target;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DemoMain {
    // lấy kết quả pha 1
    public static Set<Sensor> phase1(String url){
        Set<Target> targets = Target.readFileTarget(url);
        Set<Sensor> sensors = KCoverage.buildSensor(targets);
        HashMap<Sensor, Integer> mapWS=KCoverage.weightOfSensor(sensors, targets);
        LinkedHashMap<Sensor, Integer> sortedMap = KCoverage.sortHashMapByValue(mapWS);
        Set<Sensor> result = KCoverage.selectSensor(sortedMap,targets);
        return result;
    }
    public static void main(String[] args) {
        String url = "C:\\Users\\NGUYEN VAN THANH\\Desktop\\target.txt";
        Set<Sensor> s = phase1(url);
        System.out.println(s.size());
    }
}
