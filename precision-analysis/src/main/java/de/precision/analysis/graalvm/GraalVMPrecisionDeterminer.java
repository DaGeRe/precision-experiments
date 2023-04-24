package de.precision.analysis.graalvm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import de.dagere.kopeme.kopemedata.Kopemedata;
import picocli.CommandLine;
import picocli.CommandLine.Option;

public class GraalVMPrecisionDeterminer implements Runnable {

   @Option(names = { "-folder", "--folder" }, description = "Folder, that contains *all* data folders for the analysis", required = true)
   private File folder;

   @Option(names = { "-endDate", "--endDate" }, description = "End date for the analysis", required = true)
   private String endDate;

   public static void main(String[] args) {
      GraalVMPrecisionDeterminer plot = new GraalVMPrecisionDeterminer();
      CommandLine cli = new CommandLine(plot);
      cli.execute(args);
   }

   @Override
   public void run() {
      Date date;
      try {
         date = DateFormat.getInstance().parse(endDate);
         System.out.println("End date: " + date);

         ComparisonFinder finder = new ComparisonFinder(folder, date);

         System.out.println("Training comparisons: " + finder.getComparisonsTraining().size());
         System.out.println("Test comparisons: " + finder.getComparisonsTest().size());

         for (Comparison comparison : finder.getComparisonsTraining().values()) {
            File folderOld = new File(folder, "measurements/" + comparison.getIdOld());
            File folderNew = new File(folder, "measurements/" + comparison.getIdNew());
            
            System.out.println("Reading " + folderOld);
            Kopemedata dataOld = GraalVMReadUtil.readData(folderOld);
            Kopemedata dataNew = GraalVMReadUtil.readData(folderNew);
         }
         
      } catch (ParseException | IOException e1) {
         e1.printStackTrace();
      }
      String line;

      // while (l)

      for (File file : folder.listFiles()) {

      }

   }
}
