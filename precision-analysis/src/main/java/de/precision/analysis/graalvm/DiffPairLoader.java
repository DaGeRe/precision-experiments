package de.precision.analysis.graalvm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.math3.stat.inference.TTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.VMResult;
import de.dagere.peass.measurement.statistics.Relation;
import de.dagere.peass.measurement.statistics.StatisticUtil;
import de.dagere.peass.measurement.statistics.bimodal.CompareData;

public class DiffPairLoader {

   private static final Logger LOG = LogManager.getLogger(DiffPairLoader.class);

   private final File folder;
   private Kopemedata dataOld, dataNew;
   private Relation expected;

   public DiffPairLoader(File folder) {
      this.folder = folder;
   }

   public void loadDiffPair(Comparison comparison) throws FileNotFoundException, IOException {
      File folderPredecessor = new File(folder, "measurements/" + comparison.getIdOld());
      File folderCurrent = new File(folder, "measurements/" + comparison.getIdNew());

      System.out.println("Reading " + folderPredecessor + " " + folderCurrent);
      dataOld = GraalVMReadUtil.readData(folderPredecessor);
      dataNew = GraalVMReadUtil.readData(folderCurrent);

      CompareData data = new CompareData(dataOld.getFirstDatacollectorContent(), dataNew.getFirstDatacollectorContent());
      expected = getRealRelation(data);
   }

   private Relation getRealRelation(CompareData data) {
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
      final List<VMResult> fastShortened = StatisticUtil.shortenValues(dataOld.getFirstDatacollectorContent(), 0, iterations);
      final List<VMResult> slowShortened = StatisticUtil.shortenValues(dataNew.getFirstDatacollectorContent(), 0, iterations);

      CompareData shortenedData = new CompareData(fastShortened, slowShortened);
      return shortenedData;
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
