package de.precision.processing.compilations.analysis;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class FindCorrelations {

   public static void main(String[] args) throws IOException, InterruptedException {
      File file = new File(args[0]);
      CSVReader reader = new CSVReader(file);

      System.out.println("GC-Correlation: " + new PearsonsCorrelation().correlation(reader.values[7], reader.values[8]));
      System.out.println("Compile: " + new PearsonsCorrelation().correlation(reader.values[2], reader.values[11]));

      double[][] compilationArray = new double[reader.values[0].length][reader.headers.length - 11];
      for (int headerIndex = 11; headerIndex < reader.headers.length; headerIndex++) {
         for (int lineIndex = 0; lineIndex < reader.values.length; lineIndex++) {
            compilationArray[lineIndex][headerIndex - 11] = reader.values[lineIndex][headerIndex];
         }
      }
      
      double[] warmedUpDurations = new double[reader.values[0].length];
      for (int lineIndex = 0; lineIndex < reader.values.length; lineIndex++) {
         warmedUpDurations[lineIndex] = reader.values[2][lineIndex];
      }

      System.out.println("Lines: " + reader.values[0].length + " " + reader.headers.length);
      final OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
      regression.newSampleData(warmedUpDurations, compilationArray);
      System.out.println(regression.calculateRSquared());
      
      System.out.println(Arrays.toString(regression.estimateRegressionParameters()));
      
//      String myPCA = getPCA(reader);
//
//      final File outputFile = createPCAFile(file, myPCA);
//
//      CSVReader reader2 = new CSVReader(outputFile);
//      System.out.println(new PearsonsCorrelation().correlation(reader2.values[reader2.headers.length - 1], reader2.values[2]));
      // reader2.
   }

   private static String getPCA(CSVReader reader) {
      String myPCA = "";
      for (int i = 0; i < reader.headers.length; i++) {
         final double correlation = new PearsonsCorrelation().correlation(reader.values[i], reader.values[2]);
         if (Math.abs(correlation) > 0.01) {
            System.out.println(reader.headers[i] + "(" + i + "): " + correlation);
            if (i > 11) {
               if (correlation < 0) {
                  myPCA += "-";
               }
               // if (Math.abs(correlation) > 0.03) {
               // myPCA += "1.5*";
               // }
               if (Math.abs(correlation) > 0.05) {
                  myPCA += "3*";
               }
               myPCA += "$" + (i + 1) + "+";
            }
         }
      }
      myPCA = myPCA.substring(0, myPCA.length() - 1);
      System.out.println(myPCA);
      return myPCA;
   }

   private static File createPCAFile(File file, String myPCA) throws InterruptedException, IOException {
      ProcessBuilder pb = new ProcessBuilder("awk", "{print $0\"\"" + myPCA + "}", file.getName());
      pb.directory(file.getParentFile());

      final File outputFile = new File(file.getParentFile(), file.getName() + "_2");
      pb.redirectOutput(outputFile);
      pb.redirectError(outputFile);

      pb.start().waitFor();
      return outputFile;
   }
}
