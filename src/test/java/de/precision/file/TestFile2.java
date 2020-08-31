package de.precision.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;
import de.precision.Constants;

@PerformanceTestingClass(logFullData = true, overallTimeout = 0)
@RunWith(PerformanceTestRunnerJUnit.class)
public class TestFile2 {

   @PerformanceTest(warmupExecutions = 0, executionTimes = Constants.EXECUTIONS, logFullData = true, timeout = 0, dataCollectors = "ONLYTIME")
   @Test
   public void add() {
      final int repetition = System.getenv().containsKey("repetitions") ? Integer.parseInt(System.getenv().get("repetitions")) : 1;
      System.out.println("Executing " + repetition + " times");
      for (int i = 0; i < repetition; i++) {
         for (int index = 0; index < 11; index++) {
            tryOutput();
         }
      }
   }

   private void tryOutput() {
      try {
         final ByteArrayOutputStream baout = new ByteArrayOutputStream();
         Writer writer;

         writer = new OutputStreamWriter(baout, "US-ASCII");
         try {
            IOUtils.copy((InputStream) null, writer, "UTF8");
         } catch (final NullPointerException ex) {
         } catch (final IOException e) {
            e.printStackTrace();
         }
      } catch (final UnsupportedEncodingException e) {
         e.printStackTrace();
      }
   }
}
