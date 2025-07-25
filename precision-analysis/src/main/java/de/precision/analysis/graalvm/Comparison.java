package de.precision.analysis.graalvm;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;

import de.dagere.peass.measurement.statistics.Relation;

public class Comparison {
   
   private final String name;
   
   private final File oldFolder;
   private final File newFolder;
   private final Date dateOld;
   private final Date dateNew;

   private Relation relation;
   private double pValue;
   private final int runsOld, runsNew;
   private final int benchmark;

   public Comparison(String name, File oldFolder, File newFolder, Date dateOld, Date dateNew, int benchmark) {
      this.name = name;
      this.oldFolder = oldFolder;
      this.newFolder = newFolder;
      this.dateOld = dateOld;
      this.dateNew = dateNew;
      this.benchmark = benchmark;
      FilenameFilter regularCSVFilter = new FilenameFilter() {
         
         @Override
         public boolean accept(File dir, String name) {
            return name.endsWith(".csv") && !name.endsWith("_cleaned.csv");
         }
      };
      this.runsOld = oldFolder.listFiles(regularCSVFilter).length;
      this.runsNew = newFolder.listFiles(regularCSVFilter).length;;
      
      
      if (oldFolder == null || newFolder == null) {
         System.out.println("Old folder: " + oldFolder);
         System.out.println("New folder: " + newFolder);
         throw new RuntimeException("New or old folder was new, but thats not allowed");
      }
      if (!oldFolder.exists()) {
         throw new RuntimeException("Old folder does not exist: " + oldFolder.getAbsolutePath());
      }
      if (!newFolder.exists()) {
         throw new RuntimeException("New folder does not exist: " + newFolder.getAbsolutePath());
      }
   }

   public Date getDateOld() {
      return dateOld;
   }

   public Date getDateNew() {
      return dateNew;
   }

   public File getNewFolder() {
      return newFolder;
   }

   public File getOldFolder() {
      return oldFolder;
   }

   public String getName() {
      return name;
   }

   public int getVersionIdNew() {
      return Integer.parseInt(newFolder.getName());
   }

   public int getVersionIdOld() {
      return Integer.parseInt(oldFolder.getName());
   }

   public int getPlatformIdNew() {
      String[] parts = newFolder.getAbsolutePath().split("/");

      String platformString = parts[parts.length - 2];
      return Integer.parseInt(platformString);
   }

   public void setRelation(Relation relation) {
      this.relation = relation;
   }

   public Relation getRelation() {
      return relation;
   }
   
   public void setPValue(double pValue) {
      this.pValue = pValue;
   }
   
   public double getPValue() {
      return pValue;
   }
   
   public int getRunsNew() {
      return runsNew;
   }
   
   public int getRunsOld() {
      return runsOld;
   }
   
   public int getBenchmark() {
      return benchmark;
   }
}