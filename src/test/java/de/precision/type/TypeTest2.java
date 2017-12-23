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
public class TypeTest2 {
	
	private static final Random r = new Random();
	
	@PerformanceTest(warmupExecutions = 0, executionTimes = Constants.EXECUTIONS, logFullData = true, timeout = 0, dataCollectors = "ONLYTIME")
	@Test
	public void execute() {
		final int parseInt = System.getenv().containsKey("repetitions") ? Integer.parseInt(System.getenv().get("repetitions")) : 1;
		System.out.println("Executing " + parseInt + " times");
		for (int i = 0; i < parseInt; i++) {
			doLongStuff();
		}
	}

	private void doLongStuff() {
		long val = r.nextInt(Integer.MAX_VALUE / 2);
		long val2 = r.nextInt(Integer.MAX_VALUE / 2);
		long sum = val2 + val;
		System.out.println(sum + " " + val2);
	}
}
