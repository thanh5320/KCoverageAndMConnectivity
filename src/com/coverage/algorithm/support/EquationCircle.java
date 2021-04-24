package com.coverage.algorithm.support;

import java.util.ArrayList;
import java.util.List;

public class EquationCircle {
	// solve quadratic equation for the intersection of 2 circles
	public static List<Double> solveQuadraticEquations(double a, double b, double c) {
		List<Double> root = new ArrayList<>(); // list root of an quadratic equation
		double delta = b * b - 4 * a * c; 
		root.add((-b + Math.sqrt(delta)) / (2 * a));
		root.add((-b - Math.sqrt(delta)) / (2 * a));
		return root;
	}

	// calculate X when knowing the parameters: Serving to find intersection of 2 circles
	public static double sloveX(double y, double a, double b, double c, double d) {
		double numerator = c * c + d * d - a * a - b * b + y * (2 * b - 2 * d);
		double denominator = 2 * (c - a);
		return numerator / denominator;
	}

	// calculate parameter A of the quadratic equation
	public static double solveA(double a, double b, double c, double d) {
		double t = (b - d) / (c - a);
		return 1 + t * t;
	}

	// calculate parameter B of the quadratic equation
	public static double solveB(double a, double b, double c, double d) {
		double t1 = (b - d) / (c - a);
		double t2 = (c * c + d * d - a * a - b * b) / (2 * c - 2 * a) - a;
		return 2 * (t1 * t2 - b);
	}

	// calculate parameter C of the quadratic equation
	public static double solveC(double a, double b, double c, double d, double r) {
		double t1 = (c * c + d * d - a * a - b * b) / (2 * c - 2 * a) - a;
		return t1 * t1 + b * b - r * r;
	}
}
