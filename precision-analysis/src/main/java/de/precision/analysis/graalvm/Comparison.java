package de.precision.analysis.graalvm;

import java.util.Date;

class Comparison {
   private final int idOld;
   private final int idNew;
   private final Date dateOld;
   private final Date dateNew;

   public Comparison(int idOld, int idNew, Date dateOld, Date dateNew) {
      this.idOld = idOld;
      this.idNew = idNew;
      this.dateOld = dateOld;
      this.dateNew = dateNew;
   }

   public int getIdOld() {
      return idOld;
   }

   public int getIdNew() {
      return idNew;
   }

   public Date getDateOld() {
      return dateOld;
   }

   public Date getDateNew() {
      return dateNew;
   }
   
   

}