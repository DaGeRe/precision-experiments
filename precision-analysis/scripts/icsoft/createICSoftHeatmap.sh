
function getHeatmapData {
	index=$1
	outname=$2
	repetitions=$3
	cat de.precision.*.csv | grep "^$repetitions " \
		| awk '{print $2" "$3" "$'$index'}' \
		| sort -k 1 -k 2 -n \
		| awk -f $start/addblanks.awk \
		> $outname
}

function createOutlierRemovalHeatmap {
	start=$(pwd)
	cd repetitionHeatmaps
	gnuplot -c $start/plotOutlierRemovalHeatmap.plt
	cd $start
}

function createRepetitionHeatmaps {
	sourceFolder=$1
	goalFolder=$2
	start=$(pwd)
	
	mkdir $goalFolder
	for testcase in AddTest RAMTest SysoutTest
	do
		cd $sourceFolder/$testcase/results_noOutlierRemoval
		for repetitions in $(cat de.precision.*.csv | awk '{print $1}' | uniq | grep -v "#" | grep -v "repetitions")
		do
			echo "Creating heatmap for $repetitions repetitions $testcase"
			getHeatmapData 13 $start/$goalFolder/noOutlierRemoval_"$testcase"_"$repetitions".csv $repetitions
		done
		cd $start
		
		cd $sourceFolder/$testcase/results_outlierRemoval
		for repetitions in $(cat de.precision.*.csv | awk '{print $1}' | uniq | grep -v "#" | grep -v "repetitions")
		do
			echo "Creating heatmap for $repetitions repetitions $testcase"
			getHeatmapData 13 $start/$goalFolder/outlierRemoval_"$testcase"_"$repetitions".csv $repetitions
		done
		cd $start
	done
}

function createMergedHeatmaps {
	mergeFolder=$1
	start=$(pwd)
	cd $mergeFolder
	for size in 100 1000 10000 100000 1000000
	do
		heatmapFiles=( noOutlierRemoval_*_"$size.csv" )
		if [ -f ${heatmapFiles[0]} ]
		then
			echo $heatmapFiles
			
			java -cp ../../../build/libs/precision-analysis-all-2.13.jar \
				de.precision.analysis.heatmap.MergeHeatmaps \
				noOutlierRemoval_*_"$size.csv"
			mv result.csv noOutlierRemoval_$size.csv
			
			java -cp ../../../build/libs/precision-analysis-all-2.13.jar \
				de.precision.analysis.heatmap.MergeHeatmaps \
				outlierRemoval_*_"$size.csv"
			mv result.csv outlierRemoval_$size.csv
		fi
	done
	
	cd $start
}

if [ $# -lt 2 ]
then
	echo "Arguments missing, please specify 2 folders (sequentiel, parallel)"
	exit 1
fi

start=$(pwd)

echo "--- Creating basic heatmaps"
createRepetitionHeatmaps $1 repetitionHeatmaps
createMergedHeatmaps repetitionHeatmaps
createRepetitionHeatmaps $2 repetitionHeatmapsParallel
createMergedHeatmaps repetitionHeatmapsParallel

echo "--- Creating parallel / non parallel heatmap"
gnuplot -c plotParallelHeatmap.plt

echo "--- Creating outlier removal heatmap"
createOutlierRemovalHeatmap

echo "--- Creating all heatmap"
cd repetitionHeatmaps
gnuplot -c ../../plotAllHeatmap.plt

echo "--- Creating histogram"
cd $1/AddTest/results_noOutlierRemoval
gnuplot -c $start/plotOutlierHistogram.plt
mv histogram_outliers_en.pdf $start
