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
	THREADS=8
	for file in AddTest RAMTest SysoutTest
	do
		echo "Analyzing $file"
		
		echo "... without outlier removal"
		java -Xmx22g \
			-cp $start/../../build/libs/precision-analysis-all-2.13.jar \
			de.precision.analysis.repetitions.GeneratePrecisionPlot \
			-threads $THREADS \
			--statisticalTests ALL_NO_BIMODAL \
			--iterationResolution 100 \
			--vmResolution 100 \
			--maxVMs 20 \
			-data $file > "$file"_analysis_noOutlierRemoval.txt 
		
		echo "... with outlier removal"
		java -Xmx22g \
			-cp $start/../../build/libs/precision-analysis-all-2.13.jar \
			de.precision.analysis.repetitions.GeneratePrecisionPlot \
			-threads $THREADS \
			--statisticalTests ALL_NO_BIMODAL \
			--iterationResolution 100 \
			--vmResolution 100 \
			--maxVMs 20 \
			--outlierRemoval \
			-data $file > "$file"_analysis_outlierRemoval.txt 
	done
	wait
	cd $start
}

if [ $# -eq 1 ]
then
	echo "Assuming that the passed parameter $1 contains the default folders basic-parameter-comparison and parallel-sequential-comparison"
	basicParameterComparison=$1/basic-parameter-comparison
	parallelSequentialComparison=$1/parallel-sequential-comparison
else
	if [ $# -lt 2 ]
	then
		echo "Arguments missing, please specify 2 folders (sequentiel, parallel)"
		exit 1
	else
		basicParameterComparison=$1
		parallelSequentialComparison=$2
	fi
fi

if [ ! -d $basicParameterComparison ]
then
	echo "Assumed $basicParameterComparison is a directory, but wasn't"
	exit 1
fi
if [ ! -d $parallelSequentialComparison ]
then
	echo "Assumed $parallelSequentialComparison is a directory, but wasn't"
	exit 1 
fi


extractAll $basicParameterComparison
analyze $basicParameterComparison
extractAll $parallelSequentialComparison
analyze $parallelSequentialComparison

./createICSoftHeatmap.sh $basicParameterComparison $parallelSequentialComparison
