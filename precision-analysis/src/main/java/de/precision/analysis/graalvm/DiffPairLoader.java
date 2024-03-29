package de.precision.analysis.graalvm;

import java.io.File;
import java.util.List;

import org.apache.commons.math3.stat.inference.TTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.VMResult;
import de.dagere.peass.measurement.statistics.Relation;
import de.dagere.peass.measurement.statistics.StatisticUtil;
import de.dagere.peass.measurement.statistics.bimodal.CompareData;
import de.precision.analysis.repetitions.HistogramValueWriter;

public class DiffPairLoader {

   private static final Logger LOG = LogManager.getLogger(DiffPairLoader.class);

   private final File folder;
   private Kopemedata dataOld, dataNew;
   private Relation expected;

   public DiffPairLoader(File folder) {
      this.folder = folder;
   }

   public void loadDiffPair(Comparison comparison) {
      File folderPredecessor = comparison.getOldFolder();
      File folderCurrent = comparison.getNewFolder();

      System.out.println("Reading " + folderPredecessor + " " + folderCurrent);
      dataOld = GraalVMReadUtil.readData(folderPredecessor);
      dataNew = GraalVMReadUtil.readData(folderCurrent);
      
      CompareData data = new CompareData(dataOld.getFirstDatacollectorContent(), dataNew.getFirstDatacollectorContent());
      expected = getRealRelation(data);
      
      writeHistogramCSVs(folderPredecessor, folderCurrent);
   }

   private void writeHistogramCSVs(File folderPredecessor, File folderCurrent) {
      File histogramData = new File("results/histogram_" + folderCurrent.getName());
      if (!histogramData.exists()) {
         histogramData.mkdirs();
         HistogramValueWriter.writeValues(dataOld.getFirstDatacollectorContent(), new File(histogramData, folderPredecessor.getName() + ".csv"));
         HistogramValueWriter.writeValues(dataNew.getFirstDatacollectorContent(), new File(histogramData, folderCurrent.getName() + ".csv"));
      }
   }

   private Relation getRealRelation(CompareData data) {
      double relativeChange = Math.abs((data.getAvgCurrent() - data.getAvgPredecessor()) / data.getAvgPredecessor());
      if (relativeChange < 0.02) {
         return Relation.EQUAL;
      }
      final boolean tchange = new TTest().homoscedasticTTest(data.getPredecessor(), data.getCurrent(), 0.01);
      Relation expected;
      if (tchange) {
         if (data.getAvgPredecessor() > data.getAvgCurrent()) {
            expected = Relation.GREATER_THAN;
         } else {
            expected = Relation.LESS_THAN;
         }
      } else {
         expected = Relation.EQUAL;
      }
      return expected;
   }
   
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
