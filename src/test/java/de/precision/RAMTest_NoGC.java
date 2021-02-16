package de.precision;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;
import de.precision.workloads.ReserveRAM;

@PerformanceTestingClass(logFullData = true, overallTimeout = 0)
@RunWith(PerformanceTestRunnerJUnit.class)
public class RAMTest_NoGC {
   
   @PerformanceTest(warmup = 0, iterations = Constants.EXECUTIONS, 
         logFullData = true, useKieker = false, redirectToNull=Constants.REDIRECT, 
         timeout = 0, dataCollectors = "ONLYTIME_NOGC")
	@Test
	public void add() {
//		System.out.println("Executing " + Constants.REPETITIONS + " times");
		for (int i = 0; i < Constants.REPETITIONS; i++) {
			final ReserveRAM reserveRAM = new ReserveRAM(Constants.WORKLOADSIZE);
			reserveRAM.reserveRAM();
			final int[][] ints = reserveRAM.getInts();
			final int[] lastRow = ints[ints.length-1];
			System.out.println(lastRow[lastRow.length-1]);
		}
	}
}
