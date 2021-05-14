package com.coverage.algorithm.support;

public class Hash {
	public static int hashCode(double a, double b) {
		return (int) (a + b * 2213);
	}
	
	public static int hashCode(int a, int b) {
		return a + b * 3313;
	}
}
