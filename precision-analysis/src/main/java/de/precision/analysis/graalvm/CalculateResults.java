package de.precision.analysis.graalvm;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import de.dagere.peass.utils.Constants;
import de.precision.analysis.graalvm.resultingData.GraalConfiguration;
import de.precision.analysis.graalvm.resultingData.SimpleModel;
import de.precision.analysis.graalvm.resultingData.TrainingMetadata;

public class CalculateResults {
   public static void main(String[] args) throws StreamReadException, DatabindException, IOException {
      File folder = new File(args[0]);
      if (!folder.isDirectory()) {
         throw new RuntimeException("The argument should be a folder!");
      }

      System.out.println("#Benchmark; Real benchmark runs; Hypothetic runs; Saved runs; Type 2 error; Type 2 error > 1%; Type 2 error > 5% ");
      for (File file : folder.listFiles((FilenameFilter) new WildcardFileFilter("*.json"))) {
         SimpleModel model = Constants.OBJECTMAPPER.readValue(file, SimpleModel.class);

         GraalConfiguration graalConfiguration = model.getRuns_iterations().get(0.01d);
         if (graalConfiguration != null) {
            int peassRuns = graalConfiguration.getRuns();

            int hypothesicBenchmarkRuns = 0, realBenchmarkRuns = 0;
            for (TrainingMetadata metadata : model.getTestComparisons().values()) {
               if (realBenchmarkRuns == 0) {
                  realBenchmarkRuns += metadata.getRunsOld();
                  hypothesicBenchmarkRuns += Math.min(metadata.getRunsOld(), peassRuns);
               }
               realBenchmarkRuns += metadata.getRunsNew();
               hypothesicBenchmarkRuns += Math.min(metadata.getRunsNew(), peassRuns);
            }
            System.out.println(file +";" + realBenchmarkRuns + ";" + hypothesicBenchmarkRuns + ";" + (realBenchmarkRuns - hypothesicBenchmarkRuns) + ";" + graalConfiguration.getType2error() + ";" + graalConfiguration.getType2error_above1percent() + ";" + graalConfiguration.getType2error_above5percent());
         }
      }
   }
}
