package de.precision.sync;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;
import de.precision.Constants;
import de.precision.workloads.AddRandomNumbersSynchronized;

@PerformanceTestingClass(logFullData = true, overallTimeout = 0)
@RunWith(PerformanceTestRunnerJUnit.class)
public class SyncAddSmall2 {
   @PerformanceTest(warmup = 0, iterations = Constants.EXECUTIONS,  logFullData = true, timeout = 0, dataCollectors = "ONLYTIME")
	@Test
	public void add() {
		final int parseInt = System.getenv().containsKey("repetitions") ? Integer.parseInt(System.getenv().get("repetitions")) : 1;
		System.out.println("Executing " + parseInt + " times");
		for (int i = 0; i < parseInt; i++) {
			addSmall();
		}
	}

	private void addSmall() {
		final AddRandomNumbersSynchronized rm = new AddRandomNumbersSynchronized();
		for (int i = 0; i < 6; i++) {
			rm.addSomething();
		}
		System.out.println(rm.getValue());
	}

}
