package de.precision.processing.repetitions.misc;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jakarta.xml.bind.JAXBException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Fulldata.Value;
import de.precision.processing.util.RepetitionFolderHandler;
import de.precision.processing.util.PrecisionFolderUtil;

public class DetermineAverageTime extends RepetitionFolderHandler {

   private static final Logger LOG = LogManager.getLogger(DetermineAverageTime.class);

   public static void main(final String[] args) throws JAXBException, IOException {
      final File folder = new File(args[0]);
      PrecisionFolderUtil.processFolder(folder, (repetition_folder) -> new DetermineAverageTime(repetition_folder));

      LOG.info("Overhead: " + overheads.getMean());
      for (final Map.Entry<Integer, DescriptiveStatistics> entry : executionDurations.entrySet()) {
         LOG.info(entry.getKey() + ";" + entry.getValue().getMean());
      }
   }

   public DetermineAverageTime(final File sequenceFolder) {
      super(sequenceFolder);
   }

   static DescriptiveStatistics overheads = new DescriptiveStatistics();

   static Map<Integer, DescriptiveStatistics> executionDurations = new TreeMap<>();

   @Override
   protected void processTestcases(final Testcases versionFast, final Testcases versionSlow) {
      DescriptiveStatistics statistics = executionDurations.get(repetitions);
      if (statistics == null) {
         statistics = new DescriptiveStatistics();
         executionDurations.put(repetitions, statistics);
      }

      final long overhead = getOverhead(versionFast.getTestcase().get(0).getDatacollector().get(0).getResult(), versionSlow.getTestcase().get(0).getDatacollector().get(0).getResult());
      final double duration = getAverageDuration(versionFast.getTestcase().get(0).getDatacollector().get(0).getResult(), versionSlow.getTestcase().get(0).getDatacollector().get(0).getResult());

      overheads.addValue(overhead);

      statistics.addValue(duration);
   }

   /**
    * Calculates overhead by last measurement of one vm and first measurement of next vm
    * 
    * @param versionFast
    * @param versionSlow
    * @return
    */
   public static long getOverhead(final List<Result> versionFast, final List<Result> versionSlow) {
      final Iterator<Result> slowIt = versionFast.iterator();
      final Iterator<Result> fastIt = versionSlow.iterator();

      final List<Value> fastFullData = fastIt.next().getFulldata().getValue();
      final Long endFast = fastFullData.get(fastFullData.size() - 1).getStart();

      final List<Value> slowFullData = slowIt.next().getFulldata().getValue();
      final Long start = slowFullData.get(0).getStart();
      LOG.info("Start slow: " + start + " End slow: " + endFast);

      return start - endFast;
   }

   public static double getDurationInMS(final List<Result> versionFast, final List<Result> versionSlow) {

      long firstFast = versionFast.get(0).getDate();
      long firstSlow = versionSlow.get(0).getDate();

      Value lastFast = getLast(versionFast);
      Value lasttSlow = getLast(versionSlow);
      
      final long startTime = Math.min(firstFast, firstSlow);
      final long endTime = Math.max(lastFast.getStart(), lasttSlow.getStart());
      long duration = endTime - startTime;

      return duration;
   }

   private static Value getLast(final List<Result> version) {
      List<Value> values = version.get(0).getFulldata().getValue();
      Value last = values.get(values.size() - 1);
      return last;
   }

   /**
    * Returns the duration in seconds
    * 
    * @param versionFast
    * @param versionSlow
    * @return
    */
   public static double getAverageDuration(final List<Result> versionFast, final List<Result> versionSlow) {
      final Iterator<Result> slowIt = versionFast.iterator();
      final Iterator<Result> fastIt = versionSlow.iterator();
      final List<Value> fastFD = fastIt.next().getFulldata().getValue();
      final Long startFast = fastFD.get(0).getStart();
      final Long endFast = fastFD.get(fastFD.size() - 1).getStart();
      final double averageFast = ((double) (endFast - startFast)) / fastFD.size();

      final List<Value> slowFD = slowIt.next().getFulldata().getValue();
      final Long start = slowFD.get(0).getStart();
      final Long end = slowFD.get(slowFD.size() - 1).getStart();

      final double averageSlow = ((double) (end - start)) / slowFD.size();

      double durationInNS = (averageFast + averageSlow) / 2;
      return durationInNS;
   }
}
