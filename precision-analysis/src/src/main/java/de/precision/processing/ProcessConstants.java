package de.precision.processing;

import java.io.File;

public class ProcessConstants {
   public static final File RESULTFOLDER_SIZE_EVOLUTION = new File("results/sizeEvolution/");
   public static final File RESULTFOLDER_COV = new File("results/cov/");
   
   public static final String DATAFILE_SEPARATOR = " ";
   
   static {
      if (!RESULTFOLDER_COV.exists()) {
         RESULTFOLDER_COV.mkdirs();
      }
      if (!RESULTFOLDER_SIZE_EVOLUTION.exists()) {
         RESULTFOLDER_SIZE_EVOLUTION.mkdirs();
      }
   }
}
