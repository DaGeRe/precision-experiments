package de.precision.analysis.graalvm.loading;

import java.util.List;

import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.VMResult;
import de.dagere.peass.measurement.statistics.Relation;
import de.dagere.peass.measurement.statistics.StatisticUtil;
import de.dagere.peass.measurement.statistics.bimodal.CompareData;

public abstract class DataLoader {
   
   protected Kopemedata dataOld, dataNew;
   protected Relation expected;
   
   public CompareData getShortenedCompareData(int iterations) {
      int availableIterationsOld = getPossibleIterations(dataOld);
      int availableIterationsCurrent = getPossibleIterations(dataNew);

      int currentlyAvailableIteratins = Math.min(availableIterationsOld, Math.min(availableIterationsCurrent, iterations));
      final List<VMResult> fastShortened = StatisticUtil.shortenValues(dataOld.getFirstDatacollectorContent(), 0, currentlyAvailableIteratins);
      final List<VMResult> slowShortened = StatisticUtil.shortenValues(dataNew.getFirstDatacollectorContent(), 0, currentlyAvailableIteratins);

      CompareData shortenedData = new CompareData(fastShortened, slowShortened);
      return shortenedData;
   }
   
   private int getPossibleIterations(Kopemedata data) {
      return data.getFirstDatacollectorContent().stream()
            .mapToInt(value -> value.getFulldata().getValues().size())
            .min()
            .getAsInt();
   }
   
   public Kopemedata getDataNew() {
      return dataNew;
   }

   public Kopemedata getDataOld() {
      return dataOld;
   }

   public Relation getExpected() {
      return expected;
   }
}
