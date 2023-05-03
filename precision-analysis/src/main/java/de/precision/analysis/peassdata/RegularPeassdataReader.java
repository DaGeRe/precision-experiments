package de.precision.analysis.peassdata;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import de.dagere.kopeme.datastorage.JSONDataLoader;
import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.Kopemedata;
import de.dagere.kopeme.kopemedata.VMResult;


public class RegularPeassdataReader {
   private final Map<String, Kopemedata> testcasesV1 = new LinkedHashMap<>();
   private final Map<String, Kopemedata> testcasesV2 = new LinkedHashMap<>();

   public void read(final String slowCommitName, final File versionFile, final File testclazzFile)  {
      for (File subversionFile : versionFile.listFiles()) {
         for (File vmRun : subversionFile.listFiles((FileFilter) new OrFileFilter(new WildcardFileFilter("*xml"), new WildcardFileFilter("*json")))) {
            Kopemedata current = getTestcases(slowCommitName, testclazzFile, subversionFile, vmRun);
            
            Kopemedata data = JSONDataLoader.loadData(vmRun);
            if (current.getMethods().size() > 0) {
               List<VMResult> addableResults = data.getMethods().get(0).getDatacollectorResults().get(0).getResults();
               current.getMethods().get(0).getDatacollectorResults().get(0).getResults().addAll(addableResults);
            } else {
               current.getMethods().add(data.getMethods().get(0));
            }
         }
      }
   }

   private Kopemedata getTestcases(final String slowVersionName, final File testclazzFile, final File subversionFile, final File vmRun) {
      String testMethodName = vmRun.getName().substring(0, vmRun.getName().indexOf("_"));
      Kopemedata current;
      String testcaseName = testclazzFile.getName() + "#" + testMethodName;
      if (!subversionFile.getName().equals(slowVersionName)) {
         current = testcasesV1.get(testcaseName);
         if (current == null) {
            current = new Kopemedata("");
            testcasesV1.put(testcaseName, current);
         }
      } else {
         current = testcasesV2.get(testcaseName);
         if (current == null) {
            current = new Kopemedata("");
            testcasesV2.put(testcaseName, current);
         }
      }
      return current;
   }

   public Map<String, Kopemedata> getTestcasesV1() {
      return testcasesV1;
   }

   public Map<String, Kopemedata> getTestcasesV2() {
      return testcasesV2;
   }

   public int getRepetitions() {
      VMResult exampleResult = testcasesV1.values().iterator().next().getFirstResult();
      int repetitions = (int) exampleResult.getRepetitions();
      return repetitions;
   }

   public int getIterations() {
      VMResult exampleResult = testcasesV1.values().iterator().next().getFirstResult();
      int iterations = (int) exampleResult.getIterations();
      return iterations;
   }
   
   public int getVMs() {
      DatacollectorResult datacollector = testcasesV1.values().iterator().next().getMethods().get(0).getDatacollectorResults().get(0);
      return datacollector.getResults().size();
   }
}
