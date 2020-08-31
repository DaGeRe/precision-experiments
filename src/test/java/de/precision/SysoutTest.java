package de.precision;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;
import de.precision.workloads.WriteToSystemOut;

@PerformanceTestingClass(logFullData = true, overallTimeout = 0)
@RunWith(PerformanceTestRunnerJUnit.class)
public class SysoutTest {
   
   @PerformanceTest(warmupExecutions = 0, executionTimes = Constants.EXECUTIONS, 
         logFullData = true, useKieker = false, redirectToNull=Constants.REDIRECT, 
         timeout = 0, dataCollectors = "ONLYTIME")
	@Test
	public void sysout() {
		final int parseInt = System.getenv().containsKey("repetitions") ? Integer.parseInt(System.getenv().get("repetitions")) : 1;

//		System.out.println("Executing " + parseInt + " times");
		for (int i = 0; i < parseInt; i++) {
			sysoutSomething();
		}
	}
	
	private void sysoutSomething(){
		final WriteToSystemOut rm = new WriteToSystemOut();
		for (int i = 0; i < Constants.WORKLOADSIZE; i++) {
			rm.doSomething();
		}
		System.out.println(rm.getValue());
	}
}
