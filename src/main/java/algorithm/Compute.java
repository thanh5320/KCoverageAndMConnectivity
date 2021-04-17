package algorithm;

import model.Point;
import model.Sensor;
import model.Target;

import java.util.*;

public class Compute {
    // giải phương trình bậc 2 phục vụ tìm giao điểm 2 hình tròn
    public static List<Double> solveQuadraticEquations(double a, double b, double c){
        List<Double> listY = new ArrayList<>();
        double delta = b*b - 4*a*c;
        listY.add((-b + Math.sqrt(delta)) / (2*a));
        listY.add((-b - Math.sqrt(delta)) / (2*a));
        return listY;
    }

    // tính X khi biết các tham số: Phục vụ tìm giao 2 đường tròn
    public static double sloveX(double y, double a, double b, double c, double d){
        double numerator = c*c+d*d-a*a-b*b + y*(2*b-2*d);
        double denominator = 2*(c-a);
        return numerator/denominator;
    }

    // tính tham số A của phương trình bậc 2
    public static double solveA(double a, double b, double c, double d){
        double t = (b-d)/(c-a);
        return 1+ t*t;
    }

    // tính tham số B của phương trình bậc 2
    public static double solveB(double a, double b, double c, double d){
        double t1=(b-d)/(c-a);
        double t2=(c*c+d*d-a*a-b*b)/(2*c-2*a)-a;
        return 2*(t1*t2-b);
    }

    // tính tham số C của phương trình bậc 2
    public static double solveC(double a, double b, double c, double d, double r){
        double t1 = (c*c+d*d-a*a-b*b)/(2*c-2*a)-a;
        return t1*t1 + b*b-r*r;
    }

    // tìm thêm sensor bao phủ cho 1 target: sủ dụng cho trường hợp không có điểm giao hoặc có 1 điểm giao
    public static Sensor AddSensorOneTarget(Target t){
        double x0=t.getX()- Point.RS;
        Random rd = new Random();
        double nrd=rd.nextDouble();
        double x=x0+nrd*2*Point.RS;
        if(rd.nextBoolean()){
            double y = Math.sqrt(Point.RS*Point.RS -(x- t.getX())*(x- t.getX()))+t.getY();
            return new Sensor(x,y);
        }
        double y = -Math.sqrt(Point.RS*Point.RS -(x- t.getX())*(x- t.getX()))+t.getY();
        return new Sensor(x,y);
    }

    // thêm sersor cho 2 target: trường hợp có 2 điểm giao
    public static Sensor AddSensorTowTarget(Target t1, Target t2, Sensor s1, Sensor s2){
        double x0= s1.getX()>s2.getX() ? s2.getX() : s1.getX();
        Random rd = new Random();
        Double nrd = rd.nextDouble();
        double x= x0+nrd*Math.abs(s1.getX()- s2.getX());
        if(rd.nextBoolean()){
            double yMax;
            double yMin;
            if(s1.getY()>s2.getY()){
                yMax= s1.getX();
                yMin= s2.getY();
            } else{
                yMax=s2.getY();
                yMin= s1.getY();
            }
            double y = Math.sqrt(Point.RS*Point.RS -(x- t1.getX())*(x- t1.getX()))+t1.getY();
            if(y<=yMax && y>=yMin){
                return new Sensor(x, y);
            }
            y = -Math.sqrt(Point.RS*Point.RS -(x- t1.getX())*(x- t1.getX()))+t1.getY();
            return new Sensor(x, y);
        }
        else{
            double yMax;
            double yMin;
            if(s1.getY()>s2.getY()){
                yMax= s1.getX();
                yMin= s2.getY();
            } else{
                yMax=s2.getY();
                yMin= s1.getY();
            }
            double y = Math.sqrt(Point.RS*Point.RS -(x- t2.getX())*(x- t2.getX()))+t2.getY();
            if(y<=yMax && y>=yMin){
                return new Sensor(x, y);
            }
            y = -Math.sqrt(Point.RS*Point.RS -(x- t2.getX())*(x- t2.getX()))+t2.getY();
            return new Sensor(x, y);
        }
    }
}
