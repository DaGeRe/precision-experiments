package de.precision.exception.nothrow;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;
import de.precision.Constants;
import de.precision.workloads.ThrowSomething;

@PerformanceTestingClass(logFullData = true, overallTimeout = 0)
@RunWith(PerformanceTestRunnerJUnit.class)
public class NoThrow2Test {
   @PerformanceTest(warmup = 0, iterations = Constants.EXECUTIONS, logFullData = true, useKieker = false, timeout = 0, dataCollectors = "ONLYTIME")
   @Test
   public void add() {
      final int repetition = System.getenv().containsKey("repetitions") ? Integer.parseInt(System.getenv().get("repetitions")) : 1;
      System.out.println("Executing " + repetition + " times");
      for (int i = 0; i < repetition; i++) {
         for (int index = 0; index < 10; index++) {
            handleException();
         }
      }
   }

   private void handleException() {
      ThrowSomething doSomething = new ThrowSomething();
      doSomething.returnMe(0);
   }
}
