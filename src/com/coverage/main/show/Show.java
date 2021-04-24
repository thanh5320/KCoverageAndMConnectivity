package com.coverage.main.show;

import java.util.ArrayList;
import java.util.Set;

public class Show {
	public static <T> void print(Set<T> list) {
		String nameClass = new ArrayList<T>(list).getClass().toString();
		
		for(T t : list) {
			System.out.println(t.toString() + "\n");
		}
		
		System.out.println("size of " + nameClass + " is : " + list.size() + "\n");
		System.out.println("------------");
	}
}
