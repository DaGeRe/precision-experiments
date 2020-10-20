package de.precision.processing.repetitions.sampling;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import de.dagere.kopeme.generated.Result;
import de.peass.measurement.analysis.MultipleVMTestUtil;
import de.peass.measurement.analysis.Relation;
import de.peass.measurement.analysis.statistics.OutlierRemover;
import de.precision.analysis.repetitions.PrecisionComparer;
import de.precision.analysis.repetitions.bimodal.OutlierRemoverBimodal;
import de.precision.processing.repetitions.misc.DetermineAverageTime;

public class SamplingExecutor {
   
   private final static Random RANDOM = new Random();
   
   private SamplingConfig config;
   private final PrecisionComparer comparer;
   final SummaryStatistics averageDuration;
   
   final List<Result> fastSelected;
   final List<Result> slowSelected;

   public SamplingExecutor(SamplingConfig config, List<Result> fastShortened, List<Result> slowShortened, SummaryStatistics averageDuration, 
         PrecisionComparer comparer) {
      this.averageDuration = averageDuration;
      this.comparer = comparer;
      this.config = config;
      
      fastSelected = selectPart(fastShortened,  config.getVms());
      slowSelected = selectPart(slowShortened, config.getVms());
      
      if (config.isRemoveOutliers()) {
         new OutlierRemoverBimodal(fastSelected);
         new OutlierRemoverBimodal(slowSelected);
      }
   }
   
   public void executeComparisons(Relation expected) {
      final double duration = DetermineAverageTime.getDurationInMS(fastSelected, slowSelected);
      averageDuration.addValue(duration * config.getVms());
      comparer.executeComparisons(fastSelected, slowSelected, expected, config.getTestclazz());
   }
   
   private List<Result> selectPart(final List<Result> allValues, final int vms) {
      final List<Result> selected = new LinkedList<>();
      for (int insertion = 0; insertion < vms; insertion++) {
         final int randomVMIndex = RANDOM.nextInt(allValues.size());
         final Result randomlyPickedResult = allValues.get(randomVMIndex);
         selected.add(randomlyPickedResult);
      }
      return selected;
   }
}
