function extractAll {
	start=$(pwd)
	cd $1
	for file in AddTest RAMTest SysoutTest
	do
		echo "Extracting data from $file"
		cd $file
		for repetitionCount in 100 1000 10000 100000 1000000
		do
			if [ -f precision_$repetitionCount.tar ]
			then
				mkdir precision_$repetitionCount
				tar -xvf precision_"$repetitionCount".tar -C precision_$repetitionCount &> extract_"$file".txt
			fi
		done
		cd ..
	done
	cd $start
}

function analyze {
	start=$(pwd)
	cd $1
	echo "Starting analysis (will take at least 30 minutes)"
	for file in AddTest RAMTest SysoutTest
	do
		echo "Analyzing $file"
		java -Xmx20g \
			-cp ../precision-experiments/precision-analysis/build/libs/precision-analysis-all-2.13.jar \
			de.precision.analysis.repetitions.GeneratePrecisionPlot \
			-threads 16 \
			-data $file > "$file"_analysis.txt 2>&1 &
	done
	wait
	cd $start
}

if [ $# -lt 2 ]
then
	echo "Arguments missing, please specify 2 folders (sequentiel, parallel)"
	exit 1
fi

basicParameterComparison=$1
parallelSequentialComparison=$2

extractAll $basicParameterComparison
analyze $basicParameterComparison
extractAll $parallelSequentialComparison
analyze $parallelSequentialComparison

./createICSoftHeatmap.sh $basicParameterComparison $parallelSequentialComparison
