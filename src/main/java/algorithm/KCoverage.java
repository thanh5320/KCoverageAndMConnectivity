package algorithm;

import model.Point;
import model.Sensor;
import model.Target;

import java.util.*;

public class KCoverage {
    // xây dựng tập M các sensor ban đầu sao cho mỗi target có ít nhất k sensor bảo phủ
    public static Set<Sensor> buildSensor(Set<Target> targets){
        Set<Sensor> sennors = new HashSet<>();
        List<Target> targetList = new ArrayList<>();
        targetList.addAll(targets);
        for(int i=0;i<targetList.size()-1;i++){
            for(int j=i+1;j<targetList.size();j++){
                List<Sensor> listSensor = intersection(targetList.get(i), targetList.get(j));
                if(listSensor==null){
                    //sinh ngẫu nhiên 2k điểm cho 2 target
                    Set<Sensor> s1 = new HashSet<>();
                    Set<Sensor> s2 = new HashSet<>();
                    while(s1.size()<Point.K){
                        s1.add(Compute.AddSensorOneTarget(targetList.get(i)));
                    }
                    while(s2.size()<Point.K){
                        s2.add(Compute.AddSensorOneTarget(targetList.get(j)));
                    }
                    sennors.addAll(s1);
                    sennors.addAll(s2);
                }
                else if(listSensor.size()==1){
                    // sinh ngẫu nhiên 2*(k-1) điểm cho 2 target
                    Set<Sensor> s1 = new HashSet<>();
                    s1.addAll(listSensor);
                    Set<Sensor> s2 = new HashSet<>();
                    s2.addAll(listSensor);
                    while(s1.size()<Point.K){
                        s1.add(Compute.AddSensorOneTarget(targetList.get(i)));
                    }
                    while(s2.size()<Point.K){
                        s2.add(Compute.AddSensorOneTarget(targetList.get(j)));
                    }
                    sennors.addAll(s1);
                    sennors.addAll(s2);
                }
                else{
                    // sinh ngẫu nhiên 2(k -2) điểm cho 2 target
                    Set<Sensor> s = new HashSet<>();
                    s.addAll(listSensor);

                    while(s.size()<Point.K){
                        s.add(Compute.AddSensorTowTarget(targetList.get(i),
                                targetList.get(j), listSensor.get(0), listSensor.get(1)));
                    }
                    sennors.addAll(s);
                }
            }
        }
        return sennors;
    }

    // trả về số điểm giao của 2 đường tròn
    public static List<Sensor> intersection(Target t1, Target t2){
        List<Sensor> sensors = new ArrayList<>();
        if(Point.distance(t1, t2)<2*Point.RS){
            double a=Compute.solveA(t1.getX(), t1.getY(), t2.getX(), t2.getY());
            double b=Compute.solveB(t1.getX(), t1.getY(), t2.getX(), t2.getY());
            double c =Compute.solveC(t1.getX(), t1.getY(), t2.getX(), t2.getY(), Point.RS);
            List<Double> listY = Compute.solveQuadraticEquations(a,b,c);
            for(double y : listY){
                double x = Compute.sloveX(y, t1.getX(), t1.getY(), t2.getX(), t2.getY());
                sensors.add(new Sensor(x, y));
            }
            return sensors;
        }
        else if(Point.distance(t1,t2)==2*Point.RS){
            sensors.add(new Sensor((t1.getX()+t2.getX())/2,
                    (t1.getY()+ t2.getY())/2));
            return sensors;
        }

        else return null;
    }

    // tính trọng số hay là số target mà mỗi sensor bao phủ
    public static HashMap<Sensor, Integer> weightOfSensor(Set<Sensor> sensors, Set<Target> targets){
        HashMap<Sensor, Integer> mapWS= new HashMap();
        for(Sensor s : sensors){
            int w=0;
            for(Target t : targets){
                if(isCoverage(s,t)){
                    w+=1;
                }
            }
            mapWS.put(s, w);
        }
        return mapWS;
    }

    // trả về xem 1 sensor có bao phủ một target không
    public static boolean isCoverage(Sensor s, Target t){
        return Point.distance(s,t)<=Point.RS+0.00000001;
    }

    // sắp xếp theo trọng số của target
    public static LinkedHashMap<Sensor, Integer> sortHashMapByValue(HashMap<Sensor, Integer> hashMap){
        Set<Map.Entry<Sensor, Integer>> entries = hashMap.entrySet();

        Comparator<Map.Entry<Sensor, Integer>> comparator = new Comparator<Map.Entry<Sensor, Integer>>() {
            @Override
            public int compare(Map.Entry<Sensor, Integer> e1, Map.Entry<Sensor, Integer> e2) {
                int v1 = e1.getValue();
                int v2 = e2.getValue();
                if(v1==v2) return 0;
                else if( v1>v2) return -1;
                else return 1;
            }
        };

        List<Map.Entry<Sensor, Integer>> listEntries = new ArrayList<>(entries);
        Collections.sort(listEntries, comparator);
        LinkedHashMap<Sensor, Integer> sortedMap = new LinkedHashMap<>(listEntries.size());
        for (Map.Entry<Sensor, Integer> entry : listEntries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    // trả về set sensor tối ưu trong pha 1
    public static Set<Sensor> selectSensor(LinkedHashMap<Sensor, Integer> sensors, Set<Target> targets){
        //Set<Map.Entry<Sensor, Integer>> sortedSensor= sensors.entrySet();
        HashMap<Target, Integer> mapTarget = new HashMap<>();
        Set<Target> checkMapTarget = new HashSet<>();
        Set<Sensor> sensorSet = new HashSet<>();
        for(Target t : targets){
            mapTarget.put(t, Point.K);
        }
        for (Map.Entry<Sensor, Integer> s : sensors.entrySet()) {
            if(checkMapTarget.size()==mapTarget.size()){
                break;
            }

            for(Map.Entry<Target, Integer> t : mapTarget.entrySet()){
                if(isCoverage(s.getKey(),t.getKey()) && t.getValue()>0){
                    t.setValue(t.getValue()-1);
                    if(t.getValue()==0) {
                        checkMapTarget.add(t.getKey());
                    }
                    sensorSet.add(s.getKey());
                }
            }
        }
        return sensorSet;
    }
}
