package de.precision.processing.repetitions.sampling;

import java.util.Random;

import de.dagere.peass.config.StatisticsConfig;
import de.dagere.peass.measurement.statistics.Relation;
import de.dagere.peass.measurement.statistics.bimodal.CompareData;
import de.dagere.peass.measurement.statistics.bimodal.OutlierRemoverBimodal;
import de.precision.analysis.repetitions.PrecisionComparer;

public class SamplingExecutor {

   private final static Random RANDOM = new Random();

   private SamplingConfig config;
   private final PrecisionComparer comparer;

   private final CompareData data;


   public SamplingExecutor(final SamplingConfig config, final StatisticsConfig statisticsConfig, final CompareData origin, final PrecisionComparer comparer) {
      this.comparer = comparer;
      this.config = config;

      CompareData withOutliers = selectPart(origin.getBefore(), origin.getAfter(), config.getVms());

      if (statisticsConfig.getOutlierFactor() != 0) {
         data = OutlierRemoverBimodal.removeOutliersSimple(withOutliers, statisticsConfig.getOutlierFactor());
      } else {
         data = withOutliers;
      }
   }
   
   public SamplingExecutor(final SamplingConfig config, final StatisticsConfig statisticsConfig, final double[] beforeData, double[] afterData, final PrecisionComparer comparer) {
      this.comparer = comparer;
      this.config = config;

      CompareData withOutliers = selectPart(beforeData, afterData, config.getVms());

      if (statisticsConfig.getOutlierFactor() != 0) {
         data = OutlierRemoverBimodal.removeOutliersSimple(withOutliers, statisticsConfig.getOutlierFactor());
      } else {
         data = withOutliers;
      }
   }

   public void executeComparisons(final Relation expected) {
      comparer.executeComparisons(data, expected, config.getTestclazz());
   }

   public CompareData selectPart(final double[] beforeData, double[] afterData, final int vms) {
      double[] valuesPredecessor = pickValues(beforeData, vms);
      double[] valuesCurrent = pickValues(afterData, vms);
      CompareData result = new CompareData(valuesPredecessor, valuesCurrent);
      return result;
   }
   
   private double[] pickValues(final double[] pickableValues, final int vms) {
      double[] values = new double[vms];
      for (int insertion = 0; insertion < vms; insertion++) {
         final int randomVMIndex = RANDOM.nextInt(pickableValues.length);
         final double randomlyPickedResult = pickableValues[randomVMIndex];
         values[insertion] = randomlyPickedResult;
      }
      return values;
   }
}
