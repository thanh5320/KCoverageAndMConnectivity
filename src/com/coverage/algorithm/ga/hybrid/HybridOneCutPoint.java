package com.coverage.algorithm.ga.hybrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HybridOneCutPoint implements HybridInterface{
    @Override
    public List<int[]> generateChildren(int[] father, int[] mother, int len) {
        int[] children1 = new int[len];
        int[] children2 = new int[len];
        List<int[]> rs = new ArrayList<>();
        Random rd = new Random();
        int c = rd.nextInt(len-1) + 1;
        for(int i=0;i<len;i++){
            if(i<c){
                children1[i]=father[i];
                children2[i]=mother[i];
            }else{
                children1[i]=mother[i];
                children2[i]=father[i];
            }
        }
        rs.add(children1);
        rs.add(children2);
        return rs;
    }
}
