package de.precision.analysis.graalvm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.filefilter.WildcardFileFilter;

public class MetadataFileReader {
   
   public static SimpleDateFormat METADATA_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
   
   private final File folder;

   public MetadataFileReader(File folder) {
      this.folder = folder;
   }

   public Map<File, Date> getFileDates(){
      Map<File, Date> fileDates = new LinkedHashMap<>();
      File[] metadataFiles = folder.listFiles((FilenameFilter) new WildcardFileFilter("*_metadata.csv"));
      for (File metadataFile : metadataFiles) {
         try (BufferedReader reader = new BufferedReader(new FileReader(metadataFile))){
            
            String headline = reader.readLine();
            int pathIndex = GraalVMReadUtil.getColumnIndex(headline, "extracted_path");
            int timeIndex = GraalVMReadUtil.getColumnIndex(headline, "version_time");
            
            String line;
            while ((line = reader.readLine()) != null) {
               String[] parts = line.split(",");
               
               String path = parts[pathIndex];
               File file = new File(path);
               
               String time = parts[timeIndex];
               Date date = METADATA_TIME_FORMAT.parse(time);
               
               fileDates.put(file.getParentFile(), date);
            }
         } catch (IOException | ParseException e) {
            e.printStackTrace();
         } 
      }
      return fileDates;
   }
}
