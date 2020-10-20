package de.precision.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

class StatisticDataEntity {
   
   static int meanSize = 1;
   
   final SummaryStatistics deviation = new SummaryStatistics();
   final SummaryStatistics mean = new SummaryStatistics();
   final List<SummaryStatistics> otherMeans = new ArrayList<>();

   public StatisticDataEntity() {
      otherMeans.add(new SummaryStatistics());
   }

   public SummaryStatistics getDeviation() {
      return deviation;
   }

   public SummaryStatistics getMean() {
      return mean;
   }
   
   public List<SummaryStatistics> getOtherMeans() {
      return otherMeans;
   }

   public void addMean(double mean2) {
      mean.addValue(mean2);
      final SummaryStatistics latestMean = otherMeans.get(otherMeans.size() - 1);
      if (latestMean.getN() < 100) {
         latestMean.addValue(mean2);
      } else {
         SummaryStatistics newest = new SummaryStatistics();
         otherMeans.add(newest);
         newest.addValue(mean2);
         
         meanSize = Math.max(meanSize, otherMeans.size());
      }
   }
}

class SizeDataManager {

   private Map<Integer, Map<String, StatisticDataEntity>> sizeEntities = new TreeMap<Integer, Map<String, StatisticDataEntity>>();

   public void addDeviation(int size, String testType, double deviation) {
      StatisticDataEntity entity = getEntity(size, testType);
      entity.deviation.addValue(deviation);
   }

   public void addMean(int size, String testType, double mean) {
      StatisticDataEntity entity = getEntity(size, testType);
      entity.mean.addValue(mean);

      entity.addMean(mean);
   }

   private StatisticDataEntity getEntity(int size, String testType) {
      Map<String, StatisticDataEntity> allStats = sizeEntities.get(size);
      if (allStats == null) {
         allStats = new TreeMap<String, StatisticDataEntity>();
         sizeEntities.put(size, allStats);
      }
      StatisticDataEntity stat = allStats.get(testType);
      if (stat == null) {
         stat = new StatisticDataEntity();
         allStats.put(testType, stat);
      }
      return stat;
   }

   public Map<Integer, Map<String, StatisticDataEntity>> getValues() {
      return sizeEntities;
   }

}