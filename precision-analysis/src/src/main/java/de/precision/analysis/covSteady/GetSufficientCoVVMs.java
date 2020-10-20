package de.precision.analysis.covSteady;

import java.io.File;
import java.util.Random;

import javax.xml.bind.JAXBException;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import de.precision.analysis.IterationEvolution.CoVLoader;
import de.precision.analysis.IterationEvolution.GetIterationEvolution;

public class GetSufficientCoVVMs {
   public static void main(String[] args) throws JAXBException {
      final File parentFolder = new File(args[0]);
      final CoVLoader loader = GetIterationEvolution.loadData(parentFolder);

      for (int vmcount = 100; vmcount < loader.getResults().length * 2; vmcount += 100) {
         SummaryStatistics deviationstat = sampleVMs(loader, vmcount);
         System.out.println(vmcount + " " + deviationstat.getMean() + " +- " + deviationstat.getStandardDeviation());
      }
   }

   private static SummaryStatistics sampleVMs(final CoVLoader loader, int vmcount) {
      SummaryStatistics deviationstat = new SummaryStatistics();
      for (int repetition = 0; repetition < 10000; repetition++) {
         SummaryStatistics selectedMeans = sampleVM(loader, vmcount);
         final double relativeDeviation = selectedMeans.getStandardDeviation() / selectedMeans.getMean();
         deviationstat.addValue(relativeDeviation);
      }
      return deviationstat;
   }

   private static SummaryStatistics sampleVM(final CoVLoader loader, int vmcount) {
      SummaryStatistics selectedMeans = new SummaryStatistics();
      for (int vm = 0; vm < vmcount; vm++) {
         int index = new Random().nextInt(loader.getResults().length);
         selectedMeans.addValue(loader.getResults()[index].getAverage());
      }
      return selectedMeans;
   }
}
