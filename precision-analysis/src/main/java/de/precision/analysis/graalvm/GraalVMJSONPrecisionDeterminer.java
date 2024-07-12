package de.precision.analysis.graalvm;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.dagere.peass.utils.Constants;
import de.precision.analysis.graalvm.json.GraalVMJSONData;
import de.precision.analysis.graalvm.json.Pair;
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
      for (File inputJSON : inputJSONs) {
         System.out.println("Reading " + inputJSON);
         GraalVMJSONData data;
         try {
            data = new ObjectMapper().readValue(inputJSON, GraalVMJSONData.class);
            
            final PlottableHistogramWriter histogramWriter = new PlottableHistogramWriter(new File("plottableGraphs/" + inputJSON.getName()));
            
            Configuration config = null;
            
            JSONPairLoader loader = new JSONPairLoader();
            
            PrecisionFileManager manager = new PrecisionFileManager();
            for (double type2error : new double[] { 0.01 }) {
               ConfigurationDeterminer configDeterminer = new ConfigurationDeterminer(false, type2error, precisionConfigMixin.getConfig(), manager, 10000);
               
               for (Pair pair : data.pairs()) {
                  loader.loadDiffPair(pair);
                  configDeterminer.determineConfiguration(histogramWriter, config, loader, pair);
               }
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
}
