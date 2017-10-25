package de.precision.ram;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;
import de.precision.Constants;
import de.precision.workloads.ReserveRAM;

@PerformanceTestingClass(logFullData = true, overallTimeout = 0)
@RunWith(PerformanceTestRunnerJUnit.class)
public class RAM2Test {
	@PerformanceTest(warmupExecutions = 0, executionTimes = Constants.EXECUTIONS, logFullData = true, timeout = 0, dataCollectors = "ONLYTIME")
	@Test
	public void add() {
		final int parseInt = System.getenv().containsKey("repetitions") ? Integer.parseInt(System.getenv().get("repetitions")) : 1;
		System.out.println("Executing " + parseInt + " times");
		for (int i = 0; i < parseInt; i++) {
			final ReserveRAM reserveRAM = new ReserveRAM(11);
			reserveRAM.reserveRAM();
			final int[][] ints = reserveRAM.getInts();
			final int[] lastRow = ints[ints.length-1];
			System.out.println(lastRow[lastRow.length-1]);
		}
	}
}
