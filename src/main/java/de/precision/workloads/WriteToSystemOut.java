package de.precision.workloads;

import java.util.Random;

/**
 * Write to System.out for Benchmarking
 * 
 * @author reichelt
 *
 */
public class WriteToSystemOut {
   private final Random random = new Random();
	private static final int MAXIMUM_VALUE = 100;
	int x = 0;

	public void doSomething() {
      x += random.nextInt(MAXIMUM_VALUE);
		System.out.println(x);
	}

	public int getValue() {
		return x;
	}
}
