package de.precision.workloads;

import java.util.Random;

/**
 * Adds random numbers synchronized
 * @author reichelt
 *
 */
public class AddRandomNumbersSynchronized {
	int x = 0;

	public synchronized void addSomething() {
		x += new Random().nextInt(100);
	}

	public int getValue() {
		return x;
	}
}
