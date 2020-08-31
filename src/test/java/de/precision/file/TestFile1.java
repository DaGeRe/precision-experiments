package de.precision.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;

@PerformanceTestingClass(logFullData = true, overallTimeout = 0)
@RunWith(PerformanceTestRunnerJUnit.class)
public class TestFile1 {

   @PerformanceTest(warmupExecutions = 0, executionTimes = 1, logFullData = true, timeout = 0, dataCollectors = "ONLYTIME")
   @Test
   public void add() {
      final int repetition = System.getenv().containsKey("repetitions") ? Integer.parseInt(System.getenv().get("repetitions")) : 1;
      System.out.println("Executing " + repetition + " times");
      for (int i = 0; i < repetition; i++) {
//         for (int index = 0; index < 10; index++) {
            tryOutput();
//         }
      }
   }

   private void tryOutput() {
      try {
         final ByteArrayOutputStream baout = new ByteArrayOutputStream();
         final Writer writer = new OutputStreamWriter(baout, "US-ASCII");
         IOUtils.copy((InputStream) null, writer, "UTF8");
      } catch (final NullPointerException ex) {
      } catch (final IOException e) {
         e.printStackTrace();
      }
   }
}
