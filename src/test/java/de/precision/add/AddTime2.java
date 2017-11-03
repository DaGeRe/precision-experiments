package de.precision.add;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.time.TimeBasedTestRunner;
import de.precision.workloads.AddRandomNumbers;

@PerformanceTestingClass(logFullData = true, overallTimeout = 0)
@RunWith(TimeBasedTestRunner.class)
public class AddTime2 {

	@PerformanceTest(duration = 10000, repetitions = 100, dataCollectors = "ONLYTIME")
	@Test
	public void add() {
		addSmall();
	}

	private void addSmall() {
		final AddRandomNumbers rm = new AddRandomNumbers();
		for (int i = 0; i < 6; i++) {
			rm.addSomething();
		}
		System.out.println(rm.getValue());
	}
}
