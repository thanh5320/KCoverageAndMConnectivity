package model;

public class Point {
    // các tham số này có thể truyền vào qua tham  số dòng lệnh
    public static final double RC=20;
    public static final double RS=5;
    public static final int K = 3;
    protected double x;
    protected double y;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    // tính khoảng cách giữa 2 điểm
    public static double distance(Point a, Point b){
        double x = a.x-b.x;
        double y = a.y-b.y;
        return Math.sqrt(x*x+y*y);
   }
}
