package com.coverage.algorithm.ga.mutation;

/**
 * A mutation individual will have a part of the gene that is different 
 * from the original one.
 *
 */
public interface MutationInterface {
    public int[] mutate(int[] chromosome, int len);
}
