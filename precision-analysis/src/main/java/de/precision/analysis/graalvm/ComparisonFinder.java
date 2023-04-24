package de.precision.analysis.graalvm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class ComparisonFinder {
   private final Map<Integer, Comparison> comparisonsTraining = new TreeMap<>();
   private final Map<Integer, Comparison> comparisonsTest = new TreeMap<>();
   
   public ComparisonFinder(File folder, Date endDate) {
      File diffsFile = new File(folder, "computations/iteration_time_ns/diffs.csv");
      if (!diffsFile.exists()) {
         throw new RuntimeException("File " + diffsFile.getAbsolutePath() + " needs to exist");
      }
      
      try (BufferedReader reader = new BufferedReader(new FileReader(diffsFile))) {
         String line;
         while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            if (!"None".equals(data[0]) && !"id".equals(data[0])) {
               int idOld = Integer.parseInt(data[1]);
               int idNew = Integer.parseInt(data[3]);
               Date dateOld = DateFormat.getInstance().parse(data[2]);
               Date dateNew = DateFormat.getInstance().parse(data[4]);

               Comparison comparison = new Comparison(idOld, idNew, dateOld, dateNew);

               if (dateOld.before(endDate)) {
                  comparisonsTraining.put(idOld, comparison);
               } else {
                  comparisonsTest.put(idOld, comparison);
               }

            }
         }
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ParseException e) {
         e.printStackTrace();
      }
   }
   
   public Map<Integer, Comparison> getComparisonsTraining() {
      return comparisonsTraining;
   }
   
   public Map<Integer, Comparison> getComparisonsTest() {
      return comparisonsTest;
   }
}
