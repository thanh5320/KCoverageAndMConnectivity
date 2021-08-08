package com.coverage.algorithm.ga.mutation;

import java.util.Random;

public class BitInversionMutation implements MutationInterface{
    @Override
    public int[] mutate(int[] chromosome, int len) {
        Random rd = new Random();
        int nrd = rd.nextInt(len);
        if(chromosome[nrd]==1) chromosome[nrd] = 0;
        else chromosome[nrd] = 1;
        return chromosome;
    }
}
