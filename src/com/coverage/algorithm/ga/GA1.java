package com.coverage.algorithm.ga;

import com.coverage.algorithm.ga.hybrid.HybridInterface;
import com.coverage.algorithm.ga.hybrid.HybridOneCutPoint;
import com.coverage.algorithm.ga.mutation.BitInversionMutation;
import com.coverage.algorithm.ga.mutation.MutationInterface;
import com.coverage.main.KM;
import com.coverage.main.KMGA1;
import com.coverage.models.Point;
import com.coverage.models.Sensor;
import com.coverage.models.Target;

import java.util.*;

public class GA1 {
    private HybridInterface hybird = new HybridOneCutPoint();
    private MutationInterface mutation = new BitInversionMutation();
    int repeatLimit = 200;
    // các điểm góc của miền
    private Point topLeftPoint;
    private Point topRightPoint;
    private Point bottomLeftPoint;
    private Point bottomRightPoint;

    // bước nhảy khi tìm các điểm tiềm năng
    private double potentialPointDistanceX=(1.0*KMGA1.RS)/ KMGA1.K+10;
    private double potentialPointDistanceY=(1.0*KMGA1.RS)/ KMGA1.K+10;
    // lưu các điểm tiềm năng
    private List<Sensor> potentialPoints;
    // thế hệ hiện tại
    private List<int[]> chromosomes;// 1 là đặt sensor, 0 là không đặt sensor

    // số lượng cá thể trong quần thể
    private int numberOfChromosomes = 50;

    // tỷ lệ lấy bố mẹ và con
    private double percentOfParents=0.5;
    private double percentOfChildren=0.5;
    private double percentOfmutation = 0.2;

    // 2 tham số khi tính fitness của 1 nhiễm sắc thể
    private double w1=0.5;
    private  double w2=0.5;

    private Set<Target> targets;

    // fitness hiện tại
    Map<int[], Double> fitness;

    public GA1(Set<Target> targets) {
        this.targets = targets;
        this.chromosomes = new ArrayList<>();
        potentialPoints = new ArrayList<>();
        potentialPointBorder();
        generatePotentialPoint();
        generateChromosomes();

        this.fitness = new HashMap<>();
        for(int[] s: chromosomes){
            fitness.put(s, fitness(s));
        }
    }

    // tìm miền
    public void potentialPointBorder(){
        double minX=Double.MAX_VALUE;
        double minY=Double.MAX_VALUE;
        double maxX=Double.MIN_VALUE;
        double maxY=Double.MIN_VALUE;
        for(Target target : targets){
            if(minX>target.getX()) minX=target.getX();
            if(minY>target.getY()) minY=target.getY();
            if(maxX<target.getX()) maxX=target.getX();
            if(maxY<target.getY()) maxY=target.getY();
        }
        this.bottomLeftPoint=new Point(minX-KMGA1.RS, minY-KMGA1.RS);
        this.bottomRightPoint=new Point(maxX+KMGA1.RS, minY-KMGA1.RS);
        this.topLeftPoint=new Point(minX-KMGA1.RS, maxY+KMGA1.RS);
        this.topRightPoint=new Point(maxX+KMGA1.RS, maxY+KMGA1.RS);
    }

    // tạo điểm tiềm năng
    public void generatePotentialPoint(){
        double x;
        double y;
        double maxX = topRightPoint.getX();
        double maxY = topRightPoint.getY();

        x=bottomLeftPoint.getX();;
        while (x<=maxX){
            y=bottomLeftPoint.getY();
            while(y<=maxY){
                Sensor p = new Sensor(x, y);
                y+=potentialPointDistanceY;
                this.potentialPoints.add(p);
            }
            x+=potentialPointDistanceX;
        }
    }

    // khởi tạo quần thể ban đầu
    public void generateChromosomes(){
        int len = this.potentialPoints.size();
        for(int i=0;i<numberOfChromosomes;i++){
            int[] chromosome = new int[len];
            for(int j=0;j<len;j++){
                if(Math.random()<0.5) chromosome[j]=0;
                else chromosome[j]=1;
            }
            chromosomes.add(chromosome);
        }
    }

    // tạo quần thể mới
    public void generateNewPopulation(){
        List<int[]> rs = new ArrayList<>();
        List<int[]> newChromosomes = new ArrayList<>();

        // lai ghép
        for(int i=0;i<chromosomes.size()-1;i=i+2){
            List<int[]> child = hybird.generateChildren(chromosomes.get(i), chromosomes.get(i+1), potentialPoints.size());
            rs.addAll(child);
        }

        // đột biến
        Random random = new Random();
        double nrd = random.nextDouble();
        for(int i=0;i<numberOfChromosomes;i++){
            if(nrd<percentOfmutation) rs.set(i,mutation.mutate(rs.get(i), potentialPoints.size()));
        }

        Map<int[], Double> mapChildren = new HashMap<>();
        for(int[] s: rs){
            mapChildren.put(s, fitness(s));
        }
        LinkedHashMap<int[], Double> sortedMapParent = sortMapByValue(fitness);
        LinkedHashMap<int[], Double> sortedMapChildren = sortMapByValue(mapChildren);
        int c = 0;
        Set<Map.Entry<int[], Double>> sortedEntries = sortedMapParent.entrySet();
        for (Map.Entry<int[], Double> mapping : sortedEntries) {
            c++;
            if(c>percentOfParents*numberOfChromosomes) break;
            newChromosomes.add(mapping.getKey());
        }
        c--;

        sortedEntries = sortedMapChildren.entrySet();
        for (Map.Entry<int[], Double> mapping : sortedEntries) {
            c++;
            if(c>numberOfChromosomes) break;
            newChromosomes.add(mapping.getKey());
        }
        chromosomes = newChromosomes;
        fitness = mapChildren;
    }

    // tính fitness của một nhiễm sắc thể
    public double fitness(int[] individual){
        // tổng số điểm tiềm năng
        int p=potentialPoints.size();
        // số sensor được đặt
        int m = 0;
        for(int i=0;i<p;i++){
            if(individual[i]==1) m++;
        }

        // Objective 1
        double f1 = (m*1.0)/p;

        int sumCovCost = 0;
        for(Target target : targets){
            int c = 0;
            for(int i=0;i<p;i++){
                if(individual[i]==1 && target.isCoverage(potentialPoints.get(i))) c++;
            }
            if(c>=KMGA1.K) sumCovCost +=KMGA1.K;
            else sumCovCost+=KMGA1.K-c;
        }

        // Objective 1
        double f2 = (1.0/(targets.size()*KMGA1.K))*sumCovCost;

        double fitness = w1*(1.0-f1) + w2*f2;
        return fitness;
    }

    // sắp sếp Map
    public LinkedHashMap<int[], Double> sortMapByValue(Map<int[], Double> map){

        // Khởi tạo ra một Set entries
        Set<Map.Entry<int[], Double>> entries = map.entrySet();

        // Tạo custom Comparator
        Comparator<Map.Entry<int[], Double>> comparator = new Comparator<Map.Entry<int[], Double>>() {
            @Override
            public int compare(Map.Entry<int[], Double> e1, Map.Entry<int[], Double> e2) {
                double v1 = e1.getValue();
                double v2 = e2.getValue();
                if(v1==v2) return 0;
                else if(v1>v2) return 1;
                else return -1;
            }
        };

        // Convert Set thành List
        List<Map.Entry<int[], Double>> listEntries = new ArrayList<>(entries);


        // Sắp xếp List
        Collections.sort(listEntries, comparator);

        // Tạo một LinkedHashMap và put các entry từ List đã sắp xếp sang

        LinkedHashMap<int[], Double> sortedMap = new LinkedHashMap<>(listEntries.size());
        for (Map.Entry<int[], Double> entry : listEntries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    // chạy thuật toán với khoảng các các điểm tiềm năng cố định
    public void run(){
        int i = repeatLimit;
        System.out.println("ban đầu:");

        Set<int[]> set = fitness.keySet();
        for (int[] key : set) {
            System.out.println(key + " " + fitness.get(key));
        }

        while (i-->0){
            generateNewPopulation();
            System.out.println("lần: " +i);

            Set<int[]> set1 = fitness.keySet();
            for (int[] key : set1) {
                System.out.println(key + " " + fitness.get(key));
            }
        }
    }

    // lặp đến khi có 1 nhiễm sắc thể thỏa mãn K bao phủ trên tất cả các target
    public void runKConnect(){
        potentialPointDistanceX=KMGA1.RS;
        potentialPointDistanceY=KMGA1.RS;
        int nosMin = Integer.MAX_VALUE;
        int[] rs=null;
        Boolean b = false;
        int dem=1;
        lable:while (!b){
            System.out.println("lần lặp thứ: "+dem++);
            resetValue();
            int i = repeatLimit;
            while (i-->0){
                generateNewPopulation();
            }

            for(int[] chromosome : chromosomes){
                boolean b2=true;
                for(Target target : targets){
                    int c=0;
                    for(int j=0;j<potentialPoints.size();j++){
                        if(chromosome[j]==1 && target.isCoverage(potentialPoints.get(j))){ c++;}
                    }
                    if(c<=KMGA1.K){ b2 =false; }
                }
                if(b2==true){
                    b=true;
                    int nos=0;
                    for(int j=0;j<potentialPoints.size();j++){
                        if(chromosome[j]==1)
                            nos++;
                    }
                    if(nos<nosMin) {nosMin = nos; rs=chromosome;}
                }
            }

            potentialPointDistanceX-=10.0/dem;
            potentialPointDistanceY-=10.0/dem;
        }
        System.out.println(nosMin);
    }

    // thiết lập giá trị khi mới khởi tạo đối tượng
    public void resetValue(){
        this.chromosomes = new ArrayList<>();
        potentialPoints = new ArrayList<>();
        generatePotentialPoint();
        generateChromosomes();

        this.fitness = new HashMap<>();
        for(int[] s: chromosomes){
            fitness.put(s, fitness(s));
        }
    }
}
