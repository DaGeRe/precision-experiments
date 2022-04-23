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
import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.MeasuredValue;
import de.dagere.kopeme.kopemedata.VMResult;
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
   protected void processTestcases(final Kopemedata versionFast, final Kopemedata versionSlow) {
      DescriptiveStatistics statistics = executionDurations.get(repetitions);
      if (statistics == null) {
         statistics = new DescriptiveStatistics();
         executionDurations.put(repetitions, statistics);
      }

      final long overhead = getOverhead(versionFast.getFirstDatacollectorContent(), versionSlow.getFirstDatacollectorContent());
      final double duration = getAverageDuration(versionFast.getFirstDatacollectorContent(), versionSlow.getFirstDatacollectorContent());

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
   public static long getOverhead(final List<VMResult> versionFast, final List<VMResult> versionSlow) {
      final Iterator<VMResult> slowIt = versionFast.iterator();
      final Iterator<VMResult> fastIt = versionSlow.iterator();

      final List<MeasuredValue> fastFullData = fastIt.next().getFulldata().getValues();
      final Long endFast = fastFullData.get(fastFullData.size() - 1).getStartTime();

      final List<MeasuredValue> slowFullData = slowIt.next().getFulldata().getValues();
      final Long start = slowFullData.get(0).getStartTime();
      LOG.info("Start slow: " + start + " End slow: " + endFast);

      return start - endFast;
   }

   public static double getDurationInMS(final List<VMResult> versionFast, final List<VMResult> versionSlow) {

      long firstFast = versionFast.get(0).getDate();
      long firstSlow = versionSlow.get(0).getDate();

      MeasuredValue lastFast = getLast(versionFast);
      MeasuredValue lasttSlow = getLast(versionSlow);
      
      final long startTime = Math.min(firstFast, firstSlow);
      final long endTime = Math.max(lastFast.getStartTime(), lasttSlow.getStartTime());
      long duration = endTime - startTime;

      return duration;
   }

   private static MeasuredValue getLast(final List<VMResult> version) {
      List<MeasuredValue> values = version.get(0).getFulldata().getValues();
      MeasuredValue last = values.get(values.size() - 1);
      return last;
   }

   /**
    * Returns the duration in seconds
    * 
    * @param versionFast
    * @param versionSlow
    * @return
    */
   public static double getAverageDuration(final List<VMResult> versionFast, final List<VMResult> versionSlow) {
      final Iterator<VMResult> slowIt = versionFast.iterator();
      final Iterator<VMResult> fastIt = versionSlow.iterator();
      final List<MeasuredValue> fastFD = fastIt.next().getFulldata().getValues();
      final Long startFast = fastFD.get(0).getStartTime();
      final Long endFast = fastFD.get(fastFD.size() - 1).getStartTime();
      final double averageFast = ((double) (endFast - startFast)) / fastFD.size();

      final List<MeasuredValue> slowFD = slowIt.next().getFulldata().getValues();
      final Long start = slowFD.get(0).getStartTime();
      final Long end = slowFD.get(slowFD.size() - 1).getStartTime();

      final double averageSlow = ((double) (end - start)) / slowFD.size();

      double durationInNS = (averageFast + averageSlow) / 2;
      return durationInNS;
   }
}
