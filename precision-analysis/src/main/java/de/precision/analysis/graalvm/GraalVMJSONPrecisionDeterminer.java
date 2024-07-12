package de.precision.analysis.graalvm;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.dagere.peass.utils.Constants;
import de.precision.analysis.graalvm.json.GraalVMJSONData;
import de.precision.analysis.graalvm.json.Pair;
import de.precision.analysis.graalvm.json.Prediction;
import de.precision.analysis.graalvm.loading.DiffPairLoader;
import de.precision.analysis.graalvm.loading.JSONPairLoader;
import de.precision.analysis.heatmap.Configuration;
import de.precision.analysis.repetitions.PrecisionConfigMixin;
import picocli.CommandLine;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

public class GraalVMJSONPrecisionDeterminer implements Runnable {
   
   @Option(names = { "-inputJSON", "--inputJSON" }, description = "Input JSON file from GraalVM", required = true)
   private File inputJSONs[];
   
   @Mixin
   private PrecisionConfigMixin precisionConfigMixin;
   
   public static void main(String[] args) {
      GraalVMJSONPrecisionDeterminer plot = new GraalVMJSONPrecisionDeterminer();
      CommandLine cli = new CommandLine(plot);
      cli.execute(args);
   }

   @Override
   public void run() {
      File tempResultsFolder = new File("results/");
      tempResultsFolder.mkdir();
      
      for (File inputJSON : inputJSONs) {
         System.out.println("Reading " + inputJSON);
         GraalVMJSONData data;
         try {
            ObjectMapper objectMapper = new ObjectMapper();
            data = objectMapper.readValue(inputJSON, GraalVMJSONData.class);
            
            final PlottableHistogramWriter histogramWriter = new PlottableHistogramWriter(new File("plottableGraphs/" + inputJSON.getName()));
            
            Configuration config = null;
            
            JSONPairLoader loader = new JSONPairLoader();
            
            PrecisionFileManager manager = new PrecisionFileManager();
            for (double type2error : new double[] { 0.01 }) {
               ConfigurationDeterminer configDeterminer = new ConfigurationDeterminer(false, type2error, precisionConfigMixin.getConfig(), manager, 10000);
               
               List<Pair> pairsWithPredictions = new LinkedList<>();
               for (Pair pair : data.pairs()) {
                  loader.loadDiffPair(pair);
                  Configuration determinedConfig = configDeterminer.determineConfiguration(histogramWriter, config, loader, pair);
                  Prediction prediction = new Prediction(determinedConfig.getVMs(), determinedConfig.getVMs(), determinedConfig.getIterations(), determinedConfig.getIterations());
                  pairsWithPredictions.add(pair.newPairWithPrediction(prediction));
               }
               
               GraalVMJSONData withPredictions = data.copyWithNewPairs(pairsWithPredictions.toArray(new Pair[0]));
               
               File file = new File(inputJSON.getParentFile(), inputJSON.getName().replace("input_", "output_"));
               System.out.println("Writing final JSON to " + file.getAbsolutePath());
               objectMapper.writeValue(file, withPredictions);
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
}
