package de.precision.analysis.IterationEvolution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.ArrayUtils;

public class AggregatedLoader implements CoVLoader {

   private final File parentFolder;
   private long iterations = Long.MAX_VALUE;
   private VMExecution[] results;
   
   public AggregatedLoader(File parentFolder) {
      this.parentFolder = parentFolder;
   }
   
   @Override
   public VMExecution[] getResults() {
      return results;
   }

   @Override
   public long getIterations() {
      return iterations;
   }

   @Override
   public void load()  {
      final File[] resultFiles = parentFolder.listFiles((FileFilter) new WildcardFileFilter("*.csv"));
      final List<VMExecution> executions = new LinkedList<>();
      for (File resultFile : resultFiles) {
         if (!resultFile.getName().equals("iterationEvolution.csv") && !resultFile.getName().equals("steady_state.csv")) {
            try (BufferedReader csvReader = new BufferedReader(new FileReader(resultFile))){
               double[] primitiveValues = loadValues(csvReader);
               if (primitiveValues.length > 0) {
                  VMExecution execution = new VMExecution(primitiveValues);
                  executions.add(execution);
                  iterations = Math.min(execution.getValues().length, iterations);
               }
            } catch (FileNotFoundException e) {
               e.printStackTrace();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
      results = executions.toArray(new VMExecution[0]);
   }

   private double[] loadValues(BufferedReader csvReader) throws IOException {
      String line;
      List<Double> values = new LinkedList<>();
      while ((line = csvReader.readLine()) != null) {
         String value = line.substring(0, line.indexOf(" "));
         double duration = Double.parseDouble(value);
         values.add(duration);
      }
      double[] primitiveValues = ArrayUtils.toPrimitive(values.toArray(new Double[0]));
      return primitiveValues;
   }
   
}
