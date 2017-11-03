package de.precision.exception;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;
import de.precision.Constants;
import de.precision.workloads.ThrowSomething;

@PerformanceTestingClass(logFullData = true, overallTimeout = 0)
@RunWith(PerformanceTestRunnerJUnit.class)
public class Throw1Test {
	@PerformanceTest(warmupExecutions = 0, executionTimes = Constants.EXECUTIONS, logFullData = true, timeout = 0, dataCollectors = "ONLYTIME")
	@Test
	public void add() {
		final int repetition = System.getenv().containsKey("repetitions") ? Integer.parseInt(System.getenv().get("repetitions")) : 1;
		System.out.println("Executing " + repetition + " times");
		for (int i = 0; i < repetition; i++) {
			for (int index = 0; index < 10; index++){
				handleException();
			}
		}
	}
	
	private void handleException() {
		try {
			ThrowSomething doSomething = new ThrowSomething();
			doSomething.returnMe(1);
		} catch (RuntimeException e) {

		}
	}
}
