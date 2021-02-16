package de.precision.workloads;

import java.util.Random;

/**
 * Adds random numbers
 * 
 * @author reichelt
 *
 */
public class AddRandomNumbers {
   
   public static final Random r = new Random();
   
	int x = 0;

	public void addSomething() {
		x += r.nextInt(100);
	}

	public int getValue() {
		return x;
	}
}
