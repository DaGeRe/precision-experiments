package de.precision;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;
import de.precision.workloads.AddRandomNumbers;
import org.openjdk.jmh.infra.Blackhole;

@PerformanceTestingClass(logFullData = true, overallTimeout = 0)
@RunWith(PerformanceTestRunnerJUnit.class)
public class AddTest_Blackhole {
   
   private Blackhole blackhole;
   
   @PerformanceTest(warmupExecutions = 0, executionTimes = Constants.EXECUTIONS, 
         logFullData = true, useKieker = false, redirectToNull=Constants.REDIRECT, 
         timeout = 0, dataCollectors = "ONLYTIME_NOGC")	
   @Test
	public void add() {
      blackhole = new Blackhole("Today's password is swordfish. I understand instantiating Blackholes directly is dangerous.");
//		System.out.println("Executing " + Constants.REPETITIONS + " times");
		for (int i = 0; i < Constants.REPETITIONS; i++) {
			addSmall();
		}
      blackhole.evaporate("Yes, I am Stephen Hawking, and know a thing or two about black holes.");
	}

	private void addSmall() {
		final AddRandomNumbers rm = new AddRandomNumbers();
		for (int i = 0; i < Constants.WORKLOADSIZE; i++) {
			rm.addSomething();
		}
		blackhole.consume(rm.getValue());
	}
}
