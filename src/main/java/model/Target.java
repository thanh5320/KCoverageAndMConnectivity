package model;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Target extends  Point{

    public Target(){

    }

    public Target(double x, double y){
        this.x=x;
        this.y=y;
    }

    // đọc vào danh sách các target
    public static Set<Target> readFileTarget(String url){
        Set<Target> targets = new HashSet<>();
        File file = new File(url);
        BufferedReader reader=null;
        try {
             reader= new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            System.out.println("lỗi đọc file target");
            e.printStackTrace();
        }
        try {
            String line = reader.readLine();
            while(line!=null){
                String[] s = line.split(" ");
                targets.add(new Target(Integer.parseInt(s[0]), Integer.parseInt(s[1])));
                line=reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return targets;
    }
}
