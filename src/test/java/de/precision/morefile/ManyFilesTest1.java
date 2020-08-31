package de.precision.morefile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.annotations.PerformanceTestingClass;
import de.dagere.kopeme.junit.testrunner.PerformanceTestRunnerJUnit;
import de.precision.Constants;

@PerformanceTestingClass(logFullData = true, overallTimeout = 0)
@RunWith(PerformanceTestRunnerJUnit.class)
public class ManyFilesTest1 {

   public static final int size = System.getenv().containsKey("size") ? Integer.parseInt(System.getenv().get("size")) : 1;

   @PerformanceTest(warmupExecutions = 0, executionTimes = Constants.EXECUTIONS, repetitions = 50, logFullData = true, timeout = 0, dataCollectors = "ONLYTIME")
   @Test
   public void add() {
      // final int parseInt = System.getenv().containsKey("repetitions") ? Integer.parseInt(System.getenv().get("repetitions")) : 1;
      // System.out.println("Executing " + parseInt + " times");
      // for (int i = 0; i < parseInt; i++) {
      writeSmall();
      // }
   }

   @AfterClass
   public static void cleanup() {
      final File[] files = new File("/tmp/").listFiles(new FileFilter() {

         @Override
         public boolean accept(File pathname) {
            return pathname.getName().endsWith(".tmp_prec");
         }
      });
      for (final File f : files) {
         f.delete();
      }
   }

   private void writeSmall() {
      try {
         for (int i = 0; i < size; i++) {
            final File file = File.createTempFile("myfile", ".tmp_prec");
            try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
               writer.write(new Random().nextInt(100) + "\n");
               writer.flush();
            }
         }
      } catch (final IOException e) {
         e.printStackTrace();
      }
   }
}
