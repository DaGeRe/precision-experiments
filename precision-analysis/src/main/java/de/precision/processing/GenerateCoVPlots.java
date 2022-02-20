package de.precision.processing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Fulldata.Value;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.peass.analysis.measurement.statistics.MeanCoVData;
import de.dagere.peass.analysis.measurement.statistics.MeanCoVDataContinous;
import de.dagere.peass.measurement.statistics.data.OutlierRemover;
import de.precision.processing.util.PrecisionFolderUtil;

/**
 * Generates csv-files and gnuplot-commands for printing an graph of the values and the coefficient of variation of all result files in one folder
 * 
 * @author reichelt
 *
 */
public final class GenerateCoVPlots {

   private static final Logger LOG = LogManager.getLogger(GenerateCoVPlots.class);

   static boolean useFullData = false;

   public static void main(final String[] args) throws JAXBException, IOException {

      printConfig();

      final File folder = new File(args[0]);
      File aggregationFolder = new File(folder, "aggregated");
      aggregationFolder.mkdir();

      final int avg_count = args.length > 1 ? Integer.parseInt(args[1]) : 5000;
      int index = 1;

      try (FileWriter writer = new FileWriter(new File(aggregationFolder, "steady_state.csv"))) {
         GenerateCoVPlots generator = new GenerateCoVPlots(avg_count, aggregationFolder, writer);
         final List<File> files = PrecisionFolderUtil.getSortedFiles(folder);
         for (final File dataFile : files) {
            LOG.debug("Loading: {}", dataFile);
            if (dataFile.getParentFile().getParentFile().equals(folder)) {
               index = handleFile(index, generator, dataFile);
            }
         }
         writer.flush();
      }
   }

   private static int handleFile(int index, final GenerateCoVPlots generator, final File dataFile) throws JAXBException, IOException {
      final Kopemedata data = XMLDataLoader.loadData(dataFile, 0);
      final Testcases testclazz = data.getTestcases();
      final String parentFileName = dataFile.getParentFile().getName();
      generator.handleTestcase(testclazz.getClazz(), testclazz.getTestcase().get(0), parentFileName, index);
      index++;
      return index;
   }

   private final int avg_count;
   private final File aggregationFolder;
   private final FileWriter vmInformationWriter;

   public GenerateCoVPlots(final int avg_count, final File aggregationFolder, final FileWriter vmInformationWriter) {
      super();
      this.avg_count = avg_count;
      this.aggregationFolder = aggregationFolder;
      this.vmInformationWriter = vmInformationWriter;
   }

   public static void printConfig() {
      System.out.println("set datafile separator ';'");
      System.out.println("set terminal wxt size 600,400");
      System.out.println("set decimalsign locale \"de_DE.UTF-8\"");
      System.out.println("set y2range [0:2]");
      System.out.println("set y2tics");
      System.out.println("set xlabel 'Iteration'");
      System.out.println("set ylabel 'Zeit / {/Symbol m}'");
   }

   public void handleTestcase(final String clazzname, final TestcaseType testcase, final String type, final int index) throws IOException {
      final MeanCoVData data = useFullData ? new MeanCoVDataContinous(testcase, avg_count) : new MeanCoVData(testcase, avg_count);
      // data.printTestcaseData(GenerateCoVPlots.RESULTFOLDER);

      // printVMDeviations(clazzname, testcase, testcase.getDatacollector().get(0).getResult());

      data.printAverages(aggregationFolder, "" + index);

      final List<DescriptiveStatistics> allMeans = data.getAllMeans();
      List<DescriptiveStatistics> secondHalf = allMeans.subList(allMeans.size() / 2, allMeans.size());

      DescriptiveStatistics steadyMean = new DescriptiveStatistics();
      for (DescriptiveStatistics stat : secondHalf) {
         steadyMean.addValue(stat.getMean());
      }

      int outliers = 0;
      for (Value r : testcase.getDatacollector().get(0).getResult().get(0).getFulldata().getValue()) {
         if (r.getValue() > OutlierRemover.Z_SCORE * steadyMean.getMean()) {
            outliers++;
         }
      }

      vmInformationWriter.write(steadyMean.getMean() + " " + steadyMean.getStandardDeviation() + " " + outliers + "\n");
   }

   static void printVMDeviations(final String clazzname, final TestcaseType testcase, final List<Result> results) throws IOException {
      final File summaryFile = new File(ProcessConstants.RESULTFOLDER_COV, "deviations_" + clazzname + "_" + testcase.getName() + "_all.csv");
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(summaryFile))) {

         for (final Result result : results) {
            final DescriptiveStatistics statistics = new DescriptiveStatistics();
            result.getFulldata().getValue().forEach(value -> statistics.addValue(value.getValue()));
            writer.write(statistics.getMean() + ProcessConstants.DATAFILE_SEPARATOR + statistics.getVariance() + "\n");
         }
         writer.flush();

         System.out.println("set title 'Means and Variation for " + clazzname + "." + testcase.getName() + "'");
         System.out.println("plot '" + summaryFile.getName() + "' u 1:2 title 'Variations'");
         System.out.println();
      }
   }
}
