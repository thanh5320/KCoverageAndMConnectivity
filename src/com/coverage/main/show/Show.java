package com.coverage.main.show;

import java.util.List;

public class Show {
	public static <T> void printList(List<T> list) {
		int cnt = 0;
		System.out.println();
		for(T t : list) {
			System.out.println(cnt + " : " + t.toString() + "\n");
			cnt++;
		}
		
		System.out.println("size of LIST is : " + list.size() + "\n");
		System.out.println("------------");
	}
}
