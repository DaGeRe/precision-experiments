package de.precision;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;
import de.precision.workloads.AddRandomNumbers;

@PerformanceTestingClass(logFullData = true, overallTimeout = 0)
@RunWith(PerformanceTestRunnerJUnit.class)
public class AddTest_NoMeasurement {
   
   @PerformanceTest(warmup = 0, iterations = Constants.EXECUTIONS, 
         logFullData = true, useKieker = false, redirectToNull=Constants.REDIRECT, 
         timeout = 0, dataCollectors = "NONE")	
   @Test
	public void add() {
//		System.out.println("Executing " + Constants.REPETITIONS + " times");
		for (int i = 0; i < Constants.REPETITIONS; i++) {
			addSmall();
		}
	}

	private void addSmall() {
		final AddRandomNumbers rm = new AddRandomNumbers();
		for (int i = 0; i < Constants.WORKLOADSIZE; i++) {
			rm.addSomething();
		}
		System.out.println(rm.getValue());
	}
}
