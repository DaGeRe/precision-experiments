java -cp ../../build/libs/precision-analysis-all-2.13.jar de.precision.analysis.heatmap.MergeHeatmaps results_noOutlierRemoval_AddTest/1000.csv results_noOutlierRemoval_RAMTest/1000.csv results_noOutlierRemoval_SysoutTest/1000.csv
mv result.csv noOutlierRemoval.csv

java -cp ../../build/libs/precision-analysis-all-2.13.jar de.precision.analysis.heatmap.MergeHeatmaps results_outlierRemoval_AddTest/1000.csv results_outlierRemoval_RAMTest/1000.csv results_outlierRemoval_SysoutTest/1000.csv
mv result.csv outlierRemoval.csv

gnuplot -c plotOutlierSummary.plt
