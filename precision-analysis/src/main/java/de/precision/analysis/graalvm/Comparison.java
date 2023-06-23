package de.precision.analysis.graalvm;

import java.io.File;
import java.util.Date;

class Comparison {
   private final File oldFolder;
   private final File newFolder;
   private final Date dateOld;
   private final Date dateNew;

   public Comparison(File oldFolder, File newFolder, Date dateOld, Date dateNew) {
      this.oldFolder = oldFolder;
      this.newFolder = newFolder;
      this.dateOld = dateOld;
      this.dateNew = dateNew;
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
      return oldFolder.getName() + "-" + newFolder.getName();
   }

   public int getIdNew() {
      return Integer.parseInt(newFolder.getName());
   }
   
   

}