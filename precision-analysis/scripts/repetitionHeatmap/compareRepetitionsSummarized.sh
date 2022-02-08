mkdir sizes
for size in 100 1000 10000 100000
do
	java -cp ../../build/libs/precision-analysis-all-2.13.jar de.precision.analysis.heatmap.MergeHeatmaps results_outlierRemoval_AddTest/bimodal/$size.csv results_outlierRemoval_RAMTest/bimodal/$size.csv results_outlierRemoval_SysoutTest/bimodal/$size.csv
	mv result.csv sizes/$size.csv
done

gnuplot -c plotSummaryHeatmap.plt
