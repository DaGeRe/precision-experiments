package de.precision.analysis.graalvm.loading;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.peass.measurement.statistics.Relation;
import de.dagere.peass.measurement.statistics.bimodal.CompareData;
import de.precision.analysis.graalvm.Comparison;
import de.precision.analysis.graalvm.GraalVMReadUtil;
import de.precision.analysis.repetitions.HistogramValueWriter;

public class DiffPairLoader extends DataLoader {

   private static final Logger LOG = LogManager.getLogger(DiffPairLoader.class);

   private final boolean cleaned;
   
   
   private boolean consideredRelevant;

   public DiffPairLoader(boolean cleaned) {
      this.cleaned = cleaned;
   }

   public void loadDiffPair(Comparison comparison) {
      File folderPredecessor = comparison.getOldFolder();
      File folderCurrent = comparison.getNewFolder();

      LOG.info("Reading " + folderPredecessor + " " + folderCurrent + " (" + comparison.getName() + ")");
      dataOld = GraalVMReadUtil.readData(folderPredecessor, cleaned);
      dataNew = GraalVMReadUtil.readData(folderCurrent, cleaned);

      CompareData data = new CompareData(dataOld.getFirstDatacollectorContent(), dataNew.getFirstDatacollectorContent());

      if (Math.abs((data.getAvgCurrent() - data.getAvgPredecessor()) / data.getAvgPredecessor()) < 0.01) {
         consideredRelevant = false;
      } else {
         consideredRelevant = true;
      }

      expected = comparison.getRelation();

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

   public boolean isConsideredRelevant() {
      return consideredRelevant;
   }
}
