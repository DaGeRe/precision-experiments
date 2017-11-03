package de.precision.assertType;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.precision.Constants;

public class TypeAssertTest1 {
	
	private static final Random r = new Random();
	
	@PerformanceTest(warmupExecutions = 0, executionTimes = Constants.EXECUTIONS, logFullData = true, timeout = 0, dataCollectors = "ONLYTIME")
	@Test
	public void execute() {
		final int parseInt = System.getenv().containsKey("repetitions") ? Integer.parseInt(System.getenv().get("repetitions")) : 1;
		System.out.println("Executing " + parseInt + " times");
		for (int i = 0; i < parseInt; i++) {
			doIntStuff();
		}
	}

	private void doIntStuff() {
		long i = r.nextLong();
		int sum = r.nextInt(100000) + r.nextInt(100000) + r.nextInt(100000);
		i+=sum;
		Assert.assertEquals(i, i);
	}
}
