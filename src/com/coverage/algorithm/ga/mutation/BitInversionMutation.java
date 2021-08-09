package com.coverage.algorithm.ga.mutation;

import java.util.Random;

/**
 * Mutations change any position in a gene
 */
public class BitInversionMutation implements MutationInterface{
    @Override
    public int[] mutate(int[] chromosome, int len) {
        Random rd = new Random();
        for(int i=0;i<50;i++){
            int nrd = rd.nextInt(len);
            chromosome[nrd] = 0;
        }
        
        return chromosome;
    }
}
