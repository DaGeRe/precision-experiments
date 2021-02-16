package de.precision;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;
import de.precision.workloads.ThrowSomething;

@PerformanceTestingClass(logFullData = true, overallTimeout = 0)
@RunWith(PerformanceTestRunnerJUnit.class)
public class ThrowTest_NoGC {

   @PerformanceTest(warmup = 0, iterations = Constants.EXECUTIONS, 
         logFullData = true, useKieker = false, redirectToNull=Constants.REDIRECT, 
         timeout = 0, dataCollectors = "ONLYTIME_NOGC")
   @Test
   public void add() {
      final int repetition = System.getenv().containsKey("repetitions") ? Integer.parseInt(System.getenv().get("repetitions")) : 1;
      // System.out.println("Executing " + repetition + " times");
      for (int i = 0; i < repetition; i++) {
         for (int index = 0; index < Constants.WORKLOADSIZE; index++) {
            handleException(index % Constants.THROW_TEST_RATIO);
         }
      }
   }

   private void handleException(int index) {
      try {
         ThrowSomething doSomething = new ThrowSomething();
         doSomething.returnMe(index);
      } catch (RuntimeException e) {

      }
   }
}
