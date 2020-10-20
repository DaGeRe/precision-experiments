package de.precision.processing.repetitions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.xml.bind.JAXBException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.primitives.Doubles;

import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.Result;
import de.peass.measurement.analysis.StatisticUtil;
import de.precision.processing.ProcessConstants;
import de.precision.processing.util.RepetitionFolderHandler;
import de.precision.processing.util.PrecisionFolderUtil;

/**
 * Generates the csv-file for a plot showing how the differences change with growing repetition count. Therefore, confidence intervals are shown.
 * 
 * @author reichelt
 *
 */
public class GenerateConfidenceDiffPlot extends RepetitionFolderHandler {

   private String nameOfAll;

   public GenerateConfidenceDiffPlot(final File sequenceFolder, String nameOfAll) {
      super(sequenceFolder);
      this.nameOfAll = nameOfAll;
   }

   private static final Logger LOG = LogManager.getLogger(GenerateConfidenceDiffPlot.class);

   public final static File RESULTFOLDER = new File("results/diff/");

   static {
      RepetitionFolderHandler.clearResultFolder(RESULTFOLDER);
   }

   public static void main(final String[] args) throws JAXBException, IOException {
      for (String folderName : args) {
         final File folder = new File(folderName);

         System.out.println("set xlabel 'Wiederholungen'");
         System.out.println("set ylabel 'T'");
         System.out.println("plot 'tval.csv' title 'Additionsworkloads' w lines\n");

         System.out.println("set style fill solid 0.5 border -1");
         System.out.println("set style boxplot outliers pointtype 7");
         System.out.println("set terminal wxt size 600,400");
         System.out.println("set style data boxplot\n");
         PrecisionFolderUtil.processFolder(folder, (repetition_folder) -> new GenerateConfidenceDiffPlot(repetition_folder, folder.getName()));
      }
   }

   @Override
   public void handleVersion() throws JAXBException, IOException {
      clearCache();
      super.handleVersion();
   }

   @Override
   protected void processTestcases(final Testcases versionFast, final Testcases versionSlow) {
      final File testcaseFile = new File(RESULTFOLDER, versionFast.getClazz() + "_" + nameOfAll + ".csv");
      final File testcaseFile_tVal = new File(RESULTFOLDER, "tval_" + nameOfAll + ".csv");
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(testcaseFile, true));
            BufferedWriter writer2 = new BufferedWriter(new FileWriter(testcaseFile_tVal, true))) {
         final List<Double> slowL = new LinkedList<>();
         final List<Double> fastL = new LinkedList<>();
         buildShortenedValues(versionFast, versionSlow, writer, slowL, fastL);

         writeHistogramValues(writer, slowL, fastL);
         writer.flush();
         writeTValue(writer2, slowL, fastL);

         System.out.println("plot '" + testcaseFile.getName() + "' u (1):2:(0.5):1");
      } catch (final IOException e) {
         e.printStackTrace();
      }
   }

   private void writeTValue(BufferedWriter writer2, final List<Double> slowL, final List<Double> fastL) throws IOException {
      DescriptiveStatistics statSlow = new DescriptiveStatistics(Doubles.toArray(slowL));
      DescriptiveStatistics statFast = new DescriptiveStatistics(Doubles.toArray(fastL));
      writer2.write(repetitions + " " + new TTest().t(statSlow, statFast) + " " + statSlow.getMean() + " " + statSlow.getStandardDeviation() 
         + " " + statFast.getMean() + " " + statFast.getStandardDeviation()+ "\n");
      writer2.flush();
   }

   private void writeAdditionalAverages(BufferedWriter writer, final List<Double> slowL, final List<Double> fastL) throws IOException {
      for (int i = 0; i < 5; i++) {
         final DescriptiveStatistics slowS = new DescriptiveStatistics();
         final DescriptiveStatistics fastS = new DescriptiveStatistics();
         for (int j = 0; j < 5; j++) {
            final Random random = new Random();
            slowS.addValue(slowL.get(random.nextInt(slowL.size())));
            fastS.addValue(fastL.get(random.nextInt(fastL.size())));
         }
         writer.write("'V1 " + repetitions + "'" + ProcessConstants.DATAFILE_SEPARATOR + ((int) fastS.getMean()) + "\n");
         writer.write("'V2 " + repetitions + "'" + ProcessConstants.DATAFILE_SEPARATOR + ((int) slowS.getMean()) + "\n");
      }
   }

   private void buildShortenedValues(final Testcases versionFast, final Testcases versionSlow, BufferedWriter writer, final List<Double> slowL, final List<Double> fastL)
         throws IOException {
      final Iterator<Result> slowIt = versionSlow.getTestcase().get(0).getDatacollector().get(0).getResult().iterator();
      for (final Iterator<Result> fastIt = versionFast.getTestcase().get(0).getDatacollector().get(0).getResult().iterator(); fastIt.hasNext();) {
         final Result fast = fastIt.next();
         final Result slow = slowIt.next();
         Result fastShortened = StatisticUtil.shortenResult(fast);
         Result slowShortened = StatisticUtil.shortenResult(slow);
         slowL.add(slowShortened.getValue());
         fastL.add(fastShortened.getValue());
      }
   }

   private void writeHistogramValues(BufferedWriter writer, final List<Double> slowL, final List<Double> fastL) throws IOException {
      final Iterator<Double> slowIt = slowL.iterator();
      for (final Iterator<Double> fastIt = fastL.iterator(); fastIt.hasNext();) {
         final Double fast = fastIt.next();
         final Double slow = slowIt.next();
         writer.write("\"V1 " + repetitions + "\"" + ProcessConstants.DATAFILE_SEPARATOR + fast + "\n");
         writer.write("\"V2 " + repetitions + "\"" + ProcessConstants.DATAFILE_SEPARATOR + slow + "\n");
      }
   }
}
