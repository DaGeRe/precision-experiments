package de.precision.analysis.graalvm;

import java.io.File;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.peass.measurement.statistics.Relation;

public class MetadiffFileReader {

   private static final Logger LOG = LogManager.getLogger(MetadiffFileReader.class);

   final int runOldIndex;
   final int runNewIndex;

   private final int machineType, configuration, benchmark, versionOld, versionNew;

   // id = machine-type-configuration-benchmark-version_old

   final int pValueIndex;
   final int effectSizeIndex;
   final int benchmarkIndex;
   final int machineTypeIndex;
   final int configurationIndex;

   private final MetadataFileReader metadataReader;

   public MetadiffFileReader(String headline, MetadataFileReader metadataReader) {
      runOldIndex = GraalVMReadUtil.getColumnIndex(headline, "run_id_old");
      runNewIndex = GraalVMReadUtil.getColumnIndex(headline, "run_id_new");

      machineType = GraalVMReadUtil.getColumnIndex(headline, "machine_type");
      configuration = GraalVMReadUtil.getColumnIndex(headline, "configuration");
      benchmark = GraalVMReadUtil.getColumnIndex(headline, "benchmark");
      versionOld = GraalVMReadUtil.getColumnIndex(headline, "old_version");
      versionNew = GraalVMReadUtil.getColumnIndex(headline, "new_version");

      // id = machine-type-configuration-benchmark-version_old

      pValueIndex = GraalVMReadUtil.getColumnIndex(headline, "p_value");
      effectSizeIndex = GraalVMReadUtil.getColumnIndex(headline, "size_effect");
      benchmarkIndex = GraalVMReadUtil.getColumnIndex(headline, "benchmark");
      machineTypeIndex = GraalVMReadUtil.getColumnIndex(headline, "machine_type");
      configurationIndex = GraalVMReadUtil.getColumnIndex(headline, "configuration");

      this.metadataReader = metadataReader;
   }

   public void handleLine(ComparisonCollection comparisons, String[] parts) {
      String runOld, runNew;

      if (runOldIndex != -1 && runNewIndex != -1) {
         runOld = parts[runOldIndex];
         runNew = parts[runNewIndex];
      } else {
         String machineTypeLine = parts[machineType];
         String configurationLine = parts[configuration];
         String benchmarkLine = parts[benchmark];
         runOld = machineTypeLine + "-" + configurationLine + "-" + benchmarkLine + "-" + parts[versionOld];
         runNew = machineTypeLine + "-" + configurationLine + "-" + benchmarkLine + "-" + parts[versionNew];
      }

      double pValue = Double.parseDouble(parts[pValueIndex]);
      double effectSize = Double.parseDouble(parts[effectSizeIndex]);
      final int benchmark = Integer.parseInt(parts[benchmarkIndex]);
      final int runsOld = Integer.parseInt(parts[benchmarkIndex]);
      final int runsNew = Integer.parseInt(parts[benchmarkIndex]);
      String benchmarkKey = parts[machineTypeIndex] + "-" + parts[configurationIndex] + "-" + parts[benchmarkIndex];

      String comparisonId = runOld + "_" + runNew;

      File folderOld = metadataReader.getFileById(runOld);
      File folderNew = metadataReader.getFileById(runNew);
      Date dateOld = metadataReader.getFileDates().get(folderOld);
      Date dateNew = metadataReader.getFileDates().get(folderNew);

      if (folderOld != null && folderNew != null &&
            folderOld.exists() && folderNew.exists()) {
         Comparison comparison = new Comparison(comparisonId, folderOld, folderNew, dateOld, dateNew, benchmark, runsOld, runsNew);

         comparisons.addComparison(benchmarkKey, comparisonId, comparison);

         comparison.setPValue(pValue);
         if (pValue < 0.01) {
            if (effectSize > 0) {
               comparison.setRelation(Relation.LESS_THAN);
            } else {
               comparison.setRelation(Relation.GREATER_THAN);
            }
         } else {
            comparison.setRelation(Relation.EQUAL);
         }
      } else {
         if (comparisonId.startsWith("6-") && folderOld != null && folderNew != null) {
            LOG.trace("Did not find " + comparisonId);
            LOG.trace(folderOld.getAbsolutePath() + " " + folderOld.exists());
            LOG.trace(folderNew.getAbsolutePath() + " " + folderNew.exists());
         }
      }
   }
}
