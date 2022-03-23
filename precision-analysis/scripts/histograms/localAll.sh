baseFolder=~/daten3/diss/repos/precision-experiments/repetitionEvolution/

mkdir -p histogramData

for testcase in AddTest RAMTest SysoutTest 
do
	echo "Creating histograms $testcase"
	
	cat $baseFolder/$testcase/precision_100000/result_300_*/*.xml | grep "<value>" | tr -d "<value/> \t" > histogramData/"$testcase"_fast_100000.csv
	cat $baseFolder/$testcase/precision_100000/result_301_*/*.xml | grep "<value>" | tr -d "<value/> \t" > histogramData/"$testcase"_slow_100000.csv
	gnuplot -c plotHistogram.plt $testcase
done

