package de.precision.type;

import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;
import de.precision.Constants;

@PerformanceTestingClass(logFullData = true, overallTimeout = 0)
@RunWith(PerformanceTestRunnerJUnit.class)
public class TypeTest1 {
	
	private static final Random r = new Random();
	
	@PerformanceTest(warmup = 0, iterations = Constants.EXECUTIONS, logFullData = true, timeout = 0, dataCollectors = "ONLYTIME")
	@Test
	public void execute() {
		final int parseInt = System.getenv().containsKey("repetitions") ? Integer.parseInt(System.getenv().get("repetitions")) : 1;
		System.out.println("Executing " + parseInt + " times");
		for (int i = 0; i < parseInt; i++) {
			doIntStuff();
		}
	}

	private void doIntStuff() {
		int val = r.nextInt(Integer.MAX_VALUE / 2);
		int val2 = r.nextInt(Integer.MAX_VALUE / 2);
		int sum = val2 + val;
		System.out.println(sum + " " + val2);
	}
}
