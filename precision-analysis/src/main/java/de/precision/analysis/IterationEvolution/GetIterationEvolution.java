package de.precision.analysis.IterationEvolution;

import java.io.File;

import jakarta.xml.bind.JAXBException;

public class GetIterationEvolution {

   public static void main(String[] args) throws JAXBException {
      final File parentFolder = new File(args[0]);

      if (args.length >= 2 && args[1].equals("subfolders")) {
         for (File folder : parentFolder.listFiles()) {
            File analyzeFolder = new File(folder, "aggregated");
            if (analyzeFolder.isDirectory()) {
               analyzeFolder(analyzeFolder);
            }
         }
      } else {
         analyzeFolder(parentFolder);
      }
   }

   private static void analyzeFolder(final File parentFolder) throws JAXBException {
      System.out.println("Loading: " + parentFolder);
      final CoVLoader loader = loadData(parentFolder);

      System.out.println("Analyzing " + parentFolder);
      boolean removeOutliers = false;

      final File resultFile = new File(parentFolder, "iterationEvolution.csv");
      final IterationAnalyzer analyzer = new IterationAnalyzer(loader.getResults(), resultFile, loader.getIterations(), removeOutliers);
      analyzer.analyze();
   }

   public static CoVLoader loadData(final File parentFolder) throws JAXBException {
      final CoVLoader loader;
      if (parentFolder.getName().equals("aggregated")) {
         loader = new AggregatedLoader(parentFolder);
      } else {
         loader = new IterationLoader(parentFolder);
      }
      loader.load();
      return loader;
   }

}
