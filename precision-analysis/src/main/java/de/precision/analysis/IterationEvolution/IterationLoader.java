package de.precision.analysis.IterationEvolution;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.kopemedata.Kopemedata;

public class IterationLoader implements CoVLoader {
   private final File parentFolder;
   private long iterations = Long.MAX_VALUE;
   private final VMExecution[] results;

   public IterationLoader(File parentFolder) {
      this.parentFolder = parentFolder;
      final File[] resultFiles = parentFolder.listFiles((FileFilter) new WildcardFileFilter("result_*"));
      results = new VMExecution[resultFiles.length];
   }

   @Override
   public VMExecution[] getResults() {
      return results;
   }

   @Override
   public long getIterations() {
      return iterations;
   }

   @Override
   public void load() {
      final File[] resultFiles = parentFolder.listFiles((FileFilter) new WildcardFileFilter("result_*"));
      int i = 0;
      for (File resultFolder : resultFiles) {
         Kopemedata data = JSONDataLoader.loadData(new File(resultFolder, "add.xml"), 0);
         results[i] = new VMExecution(data.getMethods().get(0).getDatacollectorResults().get(0).getResults().get(0));
         System.out.println(data.getMethods().get(0).getDatacollectorResults().get(0).getResults() + " " + results[i].getValues().length);
         iterations = Math.min(results[i].getValues().length, iterations);
         i++;
      }
   }
}
