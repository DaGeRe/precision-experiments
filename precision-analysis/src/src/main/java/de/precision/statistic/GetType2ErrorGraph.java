package de.precision.statistic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.TDistribution;

public class GetType2ErrorGraph {
   public static void main(String[] args) {

      File folder = new File("results/type2error");
      folder.mkdirs();
      writeErrors(0.01, new File(folder, "error_001.csv"));
      writeErrors(0.1, new File(folder, "error_01.csv"));
      writeErrors(0.25, new File(folder, "error_025.csv"));
      writeErrors(0.5, new File(folder, "error_05.csv"));
      writeErrors(0.75, new File(folder, "error_75.csv"));
      writeErrors(1, new File(folder, "error_1.csv"));
      writeErrors(1.5, new File(folder, "error_15.csv"));
      writeErrors(2, new File(folder, "error_2.csv"));
      writeErrors(2.5, new File(folder, "error_25.csv"));
      writeErrors(5, new File(folder, "error_5.csv"));
      
      System.out.println("set terminal wxt size 600,400");
      System.out.println("set xlabel 'VMs'");
      System.out.println("set ylabel 'Typ-II-Fehler'");
      System.out.println("plot 'error_025.csv' w lines title 'r=0.25', "
            + "'error_05.csv' w lines title 'r=0.5', "
            + "'error_75.csv' w lines title 'r=0.75', "
            + "'error_1.csv' w lines title 'r=1', "
            + "'error_15.csv' w lines title 'r=1.5', "
            + "'error_2.csv' w lines title 'r=2'");
      
      System.out.println("plot "
            + "'error_05.csv' w lines title 'r=0.5', "
            + "'error_75.csv' w lines title 'r=0.75', "
            + "'error_1.csv' w lines title 'r=1', "
            + "'error_15.csv' w lines title 'r=1.5', "
            + "'error_2.csv' w lines title 'r=2'");
      
   }

   private static void writeErrors(final double factor, final File file) {
      try (FileWriter fw = new FileWriter(file)) {
         for (int n = 10; n <= 2000; n += 10) {
            TDistribution t = new TDistribution(n * 2 - 2);
            final double t_crit = t.inverseCumulativeProbability(0.995);

            double distributionValue = (t_crit - factor * Math.sqrt(((double) n) / 2));

            NormalDistribution nd = new NormalDistribution();
            final double guessType2Error = nd.cumulativeProbability(distributionValue);
            fw.write(n + " " + guessType2Error + "\n");
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
