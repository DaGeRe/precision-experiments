package de.precision.workloads;

import java.util.Random;

public class WriteToSystemOut {
	int x = 0;

	public void doSomething() {
		x += new Random().nextInt(100);
		System.out.println(x);
	}

	public int getValue() {
		return x;
	}
}
