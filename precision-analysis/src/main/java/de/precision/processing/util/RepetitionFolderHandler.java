package de.precision.processing.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Fulldata.Value;

/**
 * Base class for a class that processes each testcase of a repetition folder
 * 
 * @author reichelt
 *
 */
public abstract class RepetitionFolderHandler {

   private static final Logger LOG = LogManager.getLogger(RepetitionFolderHandler.class);

   public static void clearResultFolder(File folder) {
      if (folder.exists()) {
         try {
            for (final File file : folder.listFiles()) {
               if (file.isDirectory()) {
                  FileUtils.deleteDirectory(file);
               } else {
                  file.delete();
               }
            }
         } catch (final IOException e) {
            e.printStackTrace();
         }
      } else {
         folder.mkdir();
      }
   }

   /**
    * Creates a instance of RepetitionFolderHandler
    * 
    * @author reichelt
    *
    */
   @FunctionalInterface
   public static interface Creator {
      public RepetitionFolderHandler createHandler(File repetitionFolder);
   }
   
   @FunctionalInterface
   public static interface CreatorParallel {
      public RepetitionFolderHandler createHandler(File repetitionFolder, ExecutorService service);
   }

   protected final int repetitions;
   private final File repetitionFolder;
   protected boolean clearStartDates = false;

   public RepetitionFolderHandler(final File repetitionFolder) {
      this.repetitionFolder = repetitionFolder;
      repetitions = Integer.parseInt(repetitionFolder.getName().substring(repetitionFolder.getName().indexOf("_") + 1));
   }

   public File getFolder() {
      return repetitionFolder;
   }

   private int slowSize, fastSize;
   protected Map<String, Testcases> testcasesV1 = null;
   protected Map<String, Testcases> testcasesV2 = null;

   public void clearCache() throws JAXBException {
      testcasesV1 = null;
      testcasesV2 = null;
      loadTestcaseData();
   }

   public void handleVersion() throws JAXBException, IOException {
      for (String testcase : testcasesV1.keySet()) {
         processTestcases(testcasesV1.get(testcase), testcasesV2.get(testcase));
      }
   }

   private void loadTestcaseData() throws JAXBException {
      testcasesV1 = new HashMap<>();
      testcasesV2 = new HashMap<>();
      LOG.debug("Loading: {} Repetitions: {}", repetitionFolder, repetitions);

      File[] files = getSortedFiles();
      LOG.debug("Files: {}", files.length);
      getWorkloadSizes(files);
      for (final File dataFile : files) {
         loadFile(dataFile);
      }
      LOG.debug("Loaded: {} - {}", testcasesV1.size(), testcasesV2.size());
   }

   private void getWorkloadSizes(File[] files) {
      slowSize = Integer.MIN_VALUE; 
      fastSize = Integer.MAX_VALUE;
      for (final File dataFile : files) {
         String resultFileName = getResultFileName(dataFile);
         int workloadSize = Integer.parseInt(resultFileName.split("_")[1]);
         if (workloadSize < fastSize) {
            fastSize = workloadSize;
         }
         if (workloadSize > slowSize) {
            slowSize = workloadSize;
         }
      }
   }

   private File[] getSortedFiles() {
      File[] files = FileUtils.listFiles(repetitionFolder, new WildcardFileFilter("*.xml"), TrueFileFilter.INSTANCE).toArray(new File[0]);
      Arrays.sort(files, new Comparator<File>() {
         public int compare(File f1, File f2) {
            return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
         }
      });
      return files;
   }

   private void loadFile(final File dataFile) throws JAXBException {
      final Kopemedata data = XMLDataLoader.loadData(dataFile, 0);
      final Testcases testclazz = data.getTestcases();
      loadGenericData(dataFile, testclazz);
   }

   private void loadGenericData(final File dataFile, final Testcases testclazz) {
      String resultFileName = getResultFileName(dataFile);
      int workloadSize = Integer.parseInt(resultFileName.split("_")[1]);
      if (workloadSize == fastSize) {
         putNewData(testclazz, testcasesV1);
      } else if (workloadSize == slowSize) {
         putNewData(testclazz, testcasesV2);
      } else {
         throw new RuntimeException("Unexpected workload size " + workloadSize + " slow size is " + slowSize + " and fast size is " + fastSize);
      }
   }

   private String getResultFileName(final File dataFile) {
      String resultFileName;
      final File parentFile = dataFile.getParentFile();
      if (!parentFile.getName().startsWith("result_")) {
         File parentFile2 = parentFile.getParentFile();
         if (parentFile2.getName().equals("precision-experiment")) {
            parentFile2 = parentFile2.getParentFile();
         }
         resultFileName = parentFile2.getName();
      } else {
         resultFileName = parentFile.getName();
      }
      return resultFileName;
   }

   private void putNewData(final Testcases testclazz, Map<String, Testcases> testcases) {
      if (clearStartDates) {
         LOG.debug("Clearing start dates");
         setValueStartsNull(testclazz);
      }
      Testcases tests = testcases.get(testclazz.getClazz());
      if (tests == null) {
         testcases.put(testclazz.getClazz(), testclazz);
      } else {
         List<Result> results = testclazz.getTestcase().get(0).getDatacollector().get(0).getResult();
         tests.getTestcase().get(0).getDatacollector().get(0).getResult().addAll(results);
      }
   }

   private void setValueStartsNull(final Testcases testclazz) {
      final List<Value> allValues = testclazz.getTestcase().get(0).getDatacollector().get(0).getResult().get(0).getFulldata().getValue();
      for (Value value : allValues.subList(1, allValues.size() - 1)) {
         value.setStart(null);
      }
   }

   protected abstract void processTestcases(Testcases versionFast, Testcases versionSlow);

}
