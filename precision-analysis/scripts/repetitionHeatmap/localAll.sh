baseFolder=~/daten3/diss/repos/precision-experiments/repetitionEvolution/

./createRepetitionHeatmap.sh $baseFolder/AddTest/results_noOutlierRemoval
./createRepetitionHeatmap.sh $baseFolder/AddTest/results_outlierRemoval

./createRepetitionHeatmap.sh $baseFolder/RAMTest/results_noOutlierRemoval
./createRepetitionHeatmap.sh $baseFolder/RAMTest/results_outlierRemoval

./createRepetitionHeatmap.sh $baseFolder/SysoutTest/results_noOutlierRemoval
./createRepetitionHeatmap.sh $baseFolder/SysoutTest/results_outlierRemoval
