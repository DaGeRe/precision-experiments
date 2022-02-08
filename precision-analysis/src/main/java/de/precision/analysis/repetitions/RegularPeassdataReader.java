package de.precision.analysis.repetitions;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.Result;

public class RegularPeassdataReader {
   private final Map<String, Testcases> testcasesV1 = new LinkedHashMap<>();
   private final Map<String, Testcases> testcasesV2 = new LinkedHashMap<>();

   public void read(final File versionFile, final File testclazzFile) throws JAXBException {
      for (File subversionFile : versionFile.listFiles()) {
         for (File vmRun : subversionFile.listFiles((FileFilter) new WildcardFileFilter("*xml"))) {
            Testcases current = getTestcases(versionFile, testclazzFile, subversionFile, vmRun);
            
            Kopemedata data = XMLDataLoader.loadData(vmRun);
            Testcases internalData = data.getTestcases();
            if (current.getTestcase().size() > 0) {
               List<Result> addableResults = internalData.getTestcase().get(0).getDatacollector().get(0).getResult();
               current.getTestcase().get(0).getDatacollector().get(0).getResult().addAll(addableResults);
            } else {
               current.getTestcase().add(internalData.getTestcase().get(0));
            }
         }
      }
   }

   private Testcases getTestcases(final File versionFile, final File testclazzFile, final File subversionFile, final File vmRun) {
      String testMethodName = vmRun.getName().substring(0, vmRun.getName().indexOf("_"));
      Testcases current;
      String testcaseName = testclazzFile.getName() + "#" + testMethodName;
      if (subversionFile.getName().equals(versionFile.getName())) {
         current = testcasesV1.get(testcaseName);
         if (current == null) {
            current = new Testcases();
            testcasesV1.put(testcaseName, current);
         }
      } else {
         current = testcasesV2.get(testcaseName);
         if (current == null) {
            current = new Testcases();
            testcasesV2.put(testcaseName, current);
         }
      }
      return current;
   }

   public Map<String, Testcases> getTestcasesV1() {
      return testcasesV1;
   }

   public Map<String, Testcases> getTestcasesV2() {
      return testcasesV2;
   }

   public int getRepetitions() {
      Result exampleResult = testcasesV1.values().iterator().next().getTestcase().get(0).getDatacollector().get(0).getResult().get(0);
      int repetitions = (int) exampleResult.getRepetitions();
      return repetitions;
   }

   public int getIterations() {
      Result exampleResult = testcasesV1.values().iterator().next().getTestcase().get(0).getDatacollector().get(0).getResult().get(0);
      int iterations = (int) exampleResult.getIterations();
      return iterations;
   }
}
