
function getHeatmapData {
	index=$1
	outname=$2
	repetitions=$3
	cat precision.csv | grep "^$repetitions " \
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

function getTestIndex {
	statisticalTest=$1
	case "$statisticalTest" in
		"MEAN")
			index=9
			;;
		"TTEST")
			index=13
			;;
		"CONFIDENCE")
			index=21
			;;
		"MANNWHITNEY")
			index=25
			;;
	esac
	echo $index
}

function createRepetitionHeatmaps {
	sourceFolder=$1
	goalFolder=$2
	start=$(pwd)
	
	mkdir $goalFolder
	
	for statisticalTest in MEAN TTEST CONFIDENCE MANNWHITNEY
	do
		index=$(getTestIndex $statisticalTest)
		for testcase in AddTest RAMTest SysoutTest
		do
			cd $sourceFolder/$testcase/results_noOutlierRemoval
			for repetitions in $(cat precision.csv | awk '{print $1}' | sort | uniq | grep -v "#" | grep -v "repetitions")
			do
				echo "Creating heatmap for $repetitions repetitions $testcase $statisticalTest $index"
				getHeatmapData $index $start/$goalFolder/noOutlierRemoval_"$testcase"_"$repetitions"_"$statisticalTest".csv $repetitions
			done
			cd $start
			
			cd $sourceFolder/$testcase/results_outlierRemoval
			for repetitions in $(cat precision.csv | awk '{print $1}' | sort | uniq | grep -v "#" | grep -v "repetitions")
			do
				echo "Creating heatmap for $repetitions repetitions $testcase $statisticalTest $index"
				getHeatmapData $index $start/$goalFolder/outlierRemoval_"$testcase"_"$repetitions"_"$statisticalTest".csv $repetitions
			done
			cd $start
		done
	done
}

function createMergedHeatmaps {
	mergeFolder=$1
	start=$(pwd)
	cd $mergeFolder
	for statisticalTest in MEAN TTEST CONFIDENCE MANNWHITNEY
	do
		for size in 100 1000 10000 100000 1000000
		do
			heatmapFiles=( noOutlierRemoval_*_"$size"_"$statisticalTest.csv" )
			if [ -f ${heatmapFiles[0]} ]
			then
				echo $heatmapFiles
				
				java -cp ../../../build/libs/precision-analysis-all-2.13.jar \
					de.precision.analysis.heatmap.MergeHeatmaps \
					noOutlierRemoval_*_"$size"_"$statisticalTest.csv"
				mv result.csv noOutlierRemoval_"$size"_"$statisticalTest.csv"
				
				java -cp ../../../build/libs/precision-analysis-all-2.13.jar \
					de.precision.analysis.heatmap.MergeHeatmaps \
					outlierRemoval_*_"$size"_"$statisticalTest.csv"
				mv result.csv outlierRemoval_"$size"_"$statisticalTest.csv"
			fi
		done
	done
	
	cd $start
}

if [ $# -lt 2 ]
then
	echo "Arguments missing, please specify 2 folders (sequentiel, parallel)"
	exit 1
fi

sequentialFolder=$1
parallelFolder=$2

start=$(pwd)

echo "--- Sorting data"
createRepetitionHeatmaps $sequentialFolder repetitionHeatmaps

echo "--- Sorting parallel data"
createRepetitionHeatmaps $parallelFolder repetitionHeatmapsParallel


echo "--- Creating basic heatmaps"
createMergedHeatmaps repetitionHeatmaps

echo "--- Creating basic parallel heatmaps"
createMergedHeatmaps repetitionHeatmapsParallel

echo "--- Creating parallel / non parallel heatmap"
gnuplot -c plotParallelHeatmap.plt

echo "--- Creating outlier removal heatmap"
createOutlierRemovalHeatmap

echo "--- Creating all heatmap"
cd repetitionHeatmaps
gnuplot -c ../../plotAllHeatmap.plt
cd $start

echo "--- Creating histogram"
cd $1/AddTest/results_noOutlierRemoval
gnuplot -c $start/plotOutlierHistogram.plt
mv histogram_outliers_en.pdf $start
