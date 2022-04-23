package de.precision.processing.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.precision.processing.util.RepetitionFolderHandler.Creator;
import de.precision.processing.util.RepetitionFolderHandler.CreatorParallel;

/**
 * Helper class for processing folders
 * 
 * @author reichelt
 *
 */
public final class PrecisionFolderUtil {

   private static final Logger LOG = LogManager.getLogger(PrecisionFolderUtil.class);

   private PrecisionFolderUtil() {

   }

   public static void processFolder(final File folder, final Creator creator) throws IOException {
      processFolder(folder, creator, "results", "precision");
   }

   /**
    * Processes all repetition-folders in the given folder in the order of their repetition count by using the Creator
    * 
    * @param folder Folder to look for repetition folders
    * @param creator Creator for ReptitionFolderHandler
    */
   public static void processFolder(final File folder, final Creator creator, String... prefixes) throws IOException {
      final File[] repetitionFolders = folder.listFiles();
      final String pattern = buildPattern(prefixes);
      sortFolders(repetitionFolders, pattern, prefixes);
      for (final File subfolder : repetitionFolders) {
         if (subfolder.getName().matches(pattern)) {
            final RepetitionFolderHandler handler = creator.createHandler(subfolder);
            handler.handleVersion();
         }
      }
   }

   public static void processFolderParallel(final File folder, final CreatorParallel creator, int threads) throws IOException, InterruptedException {
      final File[] repetitionFolders = folder.listFiles();
      final String pattern = buildPattern("precision");
      sortFolders(repetitionFolders, pattern, "precision");

      ExecutorService pool = Executors.newFixedThreadPool(threads);

      for (final File subfolder : repetitionFolders) {
         if (subfolder.getName().matches(pattern)) {
            final RepetitionFolderHandler handler = creator.createHandler(subfolder, pool);
            if (handler != null) {
               handler.handleVersion();
            }
         }
      }

      if (pool instanceof ThreadPoolExecutor) {
         final ThreadPoolExecutor threadPool = (ThreadPoolExecutor) pool;
         LOG.info("Active Threads: " + threadPool.getActiveCount() + " Overall size: " + threadPool.getPoolSize());
      } else {
         LOG.info("Pool type: {}", pool.getClass());
      }

      pool.shutdown();
      pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
   }

   private static String buildPattern(String... prefixes) {
      String pattern = "";
      for (String prefix : prefixes) {
         pattern += prefix + "_[0-9]+|";
      }
      final String pattern2 = pattern.substring(0, pattern.length() - 1);
      return pattern2;
   }

   private static void sortFolders(final File[] repetitionFolders, final String pattern, String... prefixes) {
      System.out.println(pattern);
      Arrays.sort(repetitionFolders, new Comparator<File>() {

         @Override
         public int compare(final File o1, final File o2) {

            final boolean o1Matches = o1.getName().matches(pattern);
            final boolean o2Matches = o2.getName().matches(pattern);
            if (o1Matches) {
               if (o2Matches) {
                  String prefix = findMatchingPrefix(o1, prefixes);
                  final int o1Num = Integer.parseInt(o1.getName().substring((prefix + "_").length()));
                  final int o2Num = Integer.parseInt(o2.getName().substring((prefix + "_").length()));
                  return o1Num - o2Num;
               } else {
                  System.out.println("No match: " + o1.getName() + " " + o2.getName());
                  return 10000;
               }
            } else {
               if (o2Matches) {
                  return -10000;
               } else {
                  return 0;
               }

            }
         }

         private String findMatchingPrefix(final File o1, String... prefixes) {
            String prefix = "";
            for (String prefixCandidate : prefixes) {
               if (o1.getName().contains(prefixCandidate)) {
                  prefix = prefixCandidate;
               }
            }
            return prefix;
         }
      });
   }

   public static List<File> getSortedFiles(final File folder) {
      final List<File> files = new LinkedList<>(FileUtils.listFiles(folder, new WildcardFileFilter("*.xml"), TrueFileFilter.INSTANCE));
      Collections.sort(files, new Comparator<File>() {

         @Override
         public int compare(File o1, File o2) {
            int result1Index = getResultIndex(o1);
            int result2Index = getResultIndex(o2);
            return result1Index - result2Index;
         }

         private int getResultIndex(File o1) {
            File containingFolderCandidate = o1.getParentFile();
            final String fileName;
            if (containingFolderCandidate.getParentFile().equals(folder)) {
               fileName = containingFolderCandidate.getName();
            } else {
               fileName = containingFolderCandidate.getParentFile().getName();
            }
            if (fileName.contains("_")) {
               int result1Index = Integer.parseInt(fileName.substring(fileName.indexOf("_") + 1));
               return result1Index;
            } else {
               return 0;
            }
         }
      });
      return files;
   }

}
