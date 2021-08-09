package com.coverage.algorithm.ga.hybrid;

import java.util.List;

/**
 * Class help hybrid two individual 2 parent and get out children
 */
public interface HybridInterface {
    public List<int[]> generateChildren(int[] father, int[] mother, int  len);
}
