
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

function createParallelNonParallelHeatmap {
	sequentialFolder=$1
	parallelFolder=$2

	start=$(pwd)

	test=$(echo $1 | awk -F'/' '{print $(NF-1)}')
	base=$(basename $1)

	base="parallel_"$base"_"$test

	type=$(echo $base | awk -F'_' '{print $2}')

	echo "Basefolder: $base Test: $test"

	mkdir -p $base

	cd $sequentialFolder

	mkdir -p $start/$base/bimodal/
	for repetitions in $(cat de.precision.*.csv | awk '{print $1}' | uniq | grep -v "#" | grep -v "repetitions")
	do
		echo "Creating heatmap for $repetitions repetitions"
		getHeatmapData 13 $start/$base/sequential_$repetitions.csv $repetitions
		getHeatmapData 17 $start/$base/bimodal/sequential_$repetitions.csv $repetitions
	done

	cd $parallelFolder
	for repetitions in $(cat de.precision.*.csv | awk '{print $1}' | uniq | grep -v "#" | grep -v "repetitions")
	do
		echo "Creating heatmap for $repetitions repetitions"
		getHeatmapData 13 $start/$base/parallel_$repetitions.csv $repetitions
		getHeatmapData 17 $start/$base/bimodal/parallel_$repetitions.csv $repetitions
	done

	cd $start/$base
	gnuplot -c $start/plotParallelHeatmap.plt

	mv heatmap_parallel_de.pdf "$test"_heatmap_parallel_de.pdf
	mv heatmap_parallel_en.pdf "$test"_heatmap_parallel_en.pdf
	
	cd $start
}

function createOutlierRemovalHeatmap {
	outlierRemoval=$1/results_outlierRemoval
	noOutlierRemvoal=$1/results_noOutlierRemoval
	
	start=$(pwd)

	test=$(echo $1 | awk -F'/' '{print $(NF-1)}')
	base=$(basename $1)

	base="outlier_"$base"_"$test

	type=$(echo $base | awk -F'_' '{print $2}')

	echo "Basefolder: $base Test: $test"

	mkdir -p $base

	cd $outlierRemoval
	mkdir -p $start/$base/bimodal/
	for repetitions in $(cat de.precision.*.csv | awk '{print $1}' | uniq | grep -v "#" | grep -v "repetitions")
	do
		echo "Creating heatmap for $repetitions repetitions"
		getHeatmapData 13 $start/$base/noOutlierRemoval_$repetitions.csv $repetitions
	done

	cd $noOutlierRemvoal
	for repetitions in $(cat de.precision.*.csv | awk '{print $1}' | uniq | grep -v "#" | grep -v "repetitions")
	do
		echo "Creating heatmap for $repetitions repetitions"
		getHeatmapData 13 $start/$base/outlierRemoval_$repetitions.csv $repetitions
	done

	cd $start/$base
	gnuplot -c $start/plotOutlierRemovalHeatmap.plt

	mv heatmap_outlierRemoval_de.pdf "$test"_heatmap_outlierRemoval_de.pdf
	mv heatmap_outlierRemoval_en.pdf "$test"_heatmap_outlierRemoval_en.pdf
	
	cd $start
}

function createRepetitionHeatmaps {
	start=$(pwd)
	mkdir repetitionHeatmap
	for testcase in AddTest RAMTest SysoutTest
	do
		cd $1/$testcase/results_noOutlierRemoval
		for repetitions in $(cat de.precision.*.csv | awk '{print $1}' | uniq | grep -v "#" | grep -v "repetitions")
		do
			echo "Creating heatmap for $repetitions repetitions"
			getHeatmapData 13 $start/repetitionHeatmap/noOutlierRemoval_"$testcase"_"$repetitions".csv $repetitions
		done
		cd $start
	done
}

function createMergedHeatmaps {
	start=$(pwd)
	for size in 100 1000 10000 100000 1000000
	do
		cd repetitionHeatmap
		heatmapFiles=$(ls noOutlierRemoval_*_"$size.csv")
		echo $heatmapFiles
		java -cp ../../../build/libs/precision-analysis-all-2.13.jar \
			de.precision.analysis.heatmap.MergeHeatmaps \
			noOutlierRemoval_*_"$size.csv"
		mv result.csv $size.csv
	done
	cd $start
}

if [ $# -lt 2 ]
then
	echo "Arguments missing, please specify 2 folders (sequentiel, parallel)"
	exit 1
fi

createParallelNonParallelHeatmap $1/results_outlierRemoval/AddTest $2/results_outlierRemoval/AddTest

createOutlierRemovalHeatmap $1/AddTest

createRepetitionHeatmaps $1
createMergedHeatmaps

cd repetitionHeatmap
gnuplot -c ../../plotAllHeatmap.plt

start=$(pwd)
cd $1/AddTest/results_noOutlierRemoval
gnuplot -c $start/plotOutlierHistogram.plt
mv histogram_outliers_en.pdf $start
