package de.precision.analysis.graalvm.loading;

import java.io.File;

import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.peass.measurement.statistics.Relation;
import de.precision.analysis.graalvm.GraalVMReadUtil;
import de.precision.analysis.graalvm.json.Pair;

public class JSONPairLoader extends DataLoader {
   public void loadDiffPair(Pair pair) {
      dataNew = new Kopemedata("unkownClazz");

      for (String measurementFileName : pair.newSample().measurements()) {
         File measurementFile = new File(measurementFileName);
         String commit = pair.newSample().commit();
         System.out.println(measurementFile.getAbsolutePath() + " " + measurementFile.exists());
         GraalVMReadUtil.readVersionDataFile(dataNew, measurementFile, commit);
      }

      dataOld = new Kopemedata("unkownClazz");

      for (String measurementFileName : pair.newSample().measurements()) {
         File measurementFile = new File(measurementFileName);
         String commit = pair.oldSample().commit();
         GraalVMReadUtil.readVersionDataFile(dataOld, measurementFile, commit);
      }
      
      if (pair.compareResults().regression()) {
         expected = Relation.LESS_THAN;
      } else {
         expected = Relation.GREATER_THAN;
      }
      
   }
}
