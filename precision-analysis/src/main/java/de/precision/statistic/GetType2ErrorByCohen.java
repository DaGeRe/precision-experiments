package de.precision.statistic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.math3.distribution.NormalDistribution;

public class GetType2ErrorByCohen {
   public static void main(final String[] args) {

      double significance = 0.99;
      
//      double power = 0.72;
//      getN(significance, power, 0.1);
      
      File folder = new File("results/type2error");
      folder.mkdirs();
      writeErrors(0.01, significance, new File(folder, "error_001.csv"));
      writeErrors(0.1, significance,new File(folder, "error_01.csv"));
      writeErrors(0.15, significance,new File(folder, "error_015.csv"));
      writeErrors(0.2, significance,new File(folder, "error_02.csv"));
      writeErrors(0.25, significance,new File(folder, "error_025.csv"));
      writeErrors(0.2, significance,new File(folder, "error_033.csv"));
      writeErrors(0.25, significance,new File(folder, "error_025.csv"));
      writeErrors(0.5, significance,new File(folder, "error_05.csv"));
      writeErrors(0.75, significance,new File(folder, "error_75.csv"));
      writeErrors(1, significance,new File(folder, "error_1.csv"));
      writeErrors(1.5, significance,new File(folder, "error_15.csv"));
      writeErrors(2, significance,new File(folder, "error_2.csv"));
      writeErrors(2.5, significance,new File(folder, "error_25.csv"));
      writeErrors(5, significance,new File(folder, "error_5.csv"));
      
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
      

      // double power2 = 3 - nd.cumulativeProbability(1 - (type1error / 2));
      // System.out.println("power2: " + power2);
      //
      // System.out.println(nd.inverseCumulativeProbability(0.95));
      // System.out.println(nd.inverseCumulativeProbability(0.975));
   }

   public static void writeErrors(final double effectSize, final double significance, final File file) {
      try (FileWriter fw = new FileWriter(file)) {
         for (int n = 10; n <= 5000; n += 1) {
            double type2error = 1 - getType2error(effectSize, significance, n);
            fw.write(n + " " + type2error + "\n");
         }
      } catch (IOException e) {
         e.printStackTrace();
      }

   }

   private static double getType2error(final double effectSize, final double significance, final int n) {
      NormalDistribution nd = new NormalDistribution();
      double type1error = 1 - significance;
      
      double z1 = nd.inverseCumulativeProbability(1 - (type1error/2));
      
      double fN = Math.sqrt(n / 2);
      
      double delta = effectSize * fN;
      
      double z2 = delta - z1;
      
      double power = nd.cumulativeProbability(z2);

//      System.out.println(n + " " + delta + " " +  power);
      
      return power;
   }

   private static void getN(final double significance, final double power, final double effectSize) {
      NormalDistribution nd = new NormalDistribution();
      double type1error = 1 - significance;
      double type2error = 1 - power;
      double z1 = nd.inverseCumulativeProbability(1 - (type1error / 2));
      double z2 = nd.inverseCumulativeProbability(1 - type2error);
      final double delta = z1 + z2;

      System.out.println("Delta = " + delta);

      int n = (int) (2 * Math.pow(delta / effectSize, 2));
      System.out.println("n=" + n);
   }
}
