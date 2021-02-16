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

   @PerformanceTest(warmup = 0, iterations = Constants.EXECUTIONS, 
         logFullData = true, repetitions = Constants.REPETITIONS, useKieker = false, redirectToNull = Constants.REDIRECT, 
         timeout = 0, dataCollectors = "ONLYTIME")
   @Test
   public void sysout() {
      sysoutSomething();
   }

   private void sysoutSomething() {
      final WriteToSystemOut rm = new WriteToSystemOut();
      rm.doSomething(Constants.WORKLOADSIZE);
   }
}
