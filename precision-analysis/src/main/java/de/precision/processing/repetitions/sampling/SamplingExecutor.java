package de.precision.processing.repetitions.sampling;

import java.util.Random;

import de.dagere.peass.config.StatisticsConfiguration;
import de.dagere.peass.measurement.analysis.Relation;
import de.precision.analysis.repetitions.PrecisionComparer;
import de.precision.analysis.repetitions.bimodal.CompareData;
import de.precision.analysis.repetitions.bimodal.OutlierRemoverBimodal;

public class SamplingExecutor {

   private final static Random RANDOM = new Random();

   private SamplingConfig config;
   private final PrecisionComparer comparer;

   private final CompareData data;


   public SamplingExecutor(final SamplingConfig config, final StatisticsConfiguration statisticsConfig, final CompareData origin, final PrecisionComparer comparer) {
      this.comparer = comparer;
      this.config = config;

      CompareData withOutliers = selectPart(origin, config.getVms());

      if (statisticsConfig.getOutlierFactor() != 0) {
         data = OutlierRemoverBimodal.removeOutliers(withOutliers, statisticsConfig.getOutlierFactor());
      } else {
         data = withOutliers;
      }
   }

   public void executeComparisons(final Relation expected) {
      comparer.executeComparisons(data, expected, config.getTestclazz());
   }

   public CompareData selectPart(final CompareData origin, final int vms) {
      double[] valuesPredecessor = pickValues(origin.getBefore(), vms);
      double[] valuesCurrent = pickValues(origin.getAfter(), vms);
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
