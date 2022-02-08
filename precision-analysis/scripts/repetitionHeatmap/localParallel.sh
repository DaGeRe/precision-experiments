for testcase in AddTest RAMTest SysoutTest 
do
	echo "Creating histograms $testcase"
	dataFolder=/home/reichelt/daten3/diss/repos/precision-experiment-clean/parallel/
	
	cat $dataFolder/with_noise/"$testcase"_noise/precision_10000/result_300_*/*.xml | grep "<value>" | tr -d "<value/> \t" > fast_100000.csv
	cat $dataFolder/with_noise/"$testcase"_noise/precision_10000/result_301_*/*.xml | grep "<value>" | tr -d "<value/> \t" > slow_100000.csv
	gnuplot -c plotHistogram.plt
	mv result.pdf parallel_results_outlierRemoval_$testcase/"$testcase"_histogram_noise.pdf

	cat $dataFolder/without_noise/"$testcase"_parallel/precision_10000/result_300_*/*.xml | grep "<value>" | tr -d "<value/> \t" > fast_100000.csv
	cat $dataFolder/without_noise/"$testcase"_parallel/precision_10000/result_301_*/*.xml | grep "<value>" | tr -d "<value/> \t" > slow_100000.csv
	gnuplot -c plotHistogram.plt
	mv result.pdf parallel_results_outlierRemoval_$testcase/"$testcase"_histogram_parallel.pdf


	dataFolderSequential=/home/reichelt/daten3/diss/repos/precision-experiment-clean/repetitionEvolution
	cat $dataFolderSequential/$testcase/precision_10000/result_300_*/*.xml | grep "<value>" | tr -d "<value/> \t" > fast_100000.csv
	cat $dataFolderSequential/$testcase/precision_10000/result_301_*/*.xml | grep "<value>" | tr -d "<value/> \t" > slow_100000.csv
	gnuplot -c plotHistogram.plt
	mv result.pdf parallel_results_outlierRemoval_$testcase/"$testcase"_histogram_sequential.pdf

	./createParallelHeatmap.sh \
		$dataFolderSequential/"$testcase"/results_outlierRemoval \
		$dataFolder/without_noise/"$testcase"_parallel/results_outlierRemoval  \
		$dataFolder/with_noise/"$testcase"_noise/results_outlierRemoval
done
