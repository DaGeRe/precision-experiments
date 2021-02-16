package de.precision;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;

@PerformanceTestingClass(logFullData = true, overallTimeout = 0)
@RunWith(PerformanceTestRunnerJUnit.class)
public class BaselineTest {
   
   @PerformanceTest(warmup = 0, iterations = Constants.EXECUTIONS, 
         logFullData = true, useKieker = false, redirectToNull=Constants.REDIRECT, 
         timeout = 0, dataCollectors = "ONLYTIME")  
	@Test
	public void baselinetest() {
	}
}
