package de.precision.file;

import java.io.IOException;
import java.io.Reader;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;
import de.precision.Constants;

@PerformanceTestingClass(logFullData = true, overallTimeout = 0)
@RunWith(PerformanceTestRunnerJUnit.class)
public class TestFileKoPeMe4 {

   @PerformanceTest(warmupExecutions = 0, executionTimes = Constants.EXECUTIONS, repetitions=100, logFullData = true, timeout = 0, dataCollectors = "ONLYTIME")
   @Test
   public void add() throws IOException {
      try {
         MyCopy2.copyLarge((Reader) null,  "UTF16");
      } catch (final NullPointerException ex) {
      }
   }
}

class MyCopy2 {
   private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

   public static long copyLarge(final Reader input, final String encoding) throws IOException {
      final char[] buffer = new char[DEFAULT_BUFFER_SIZE];
      long count = 0;
      int n = 0;
      while (-1 != (n = input.read(buffer))) {
         count += n;
      }
      return count;
   }
}
