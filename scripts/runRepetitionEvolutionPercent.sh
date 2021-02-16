#!/bin/bash

source functions.sh

getTests "$@"
getGlobalParameters 

if [[ -z "$2" ]] ; then
       diffPercent=1
       echo "Difference not given, taking one (1!) operation as default"
else
       diffPercent=$2
fi

if [[ -z "$BASESIZE" ]]; then
	 basesize=300
else
	 basesize=$BASESIZE
fi

testName=${tests##*.}
id=1
resultfolder=~/.KoPeMe/repetitionEvolution_"$basesize"_"$redirect"_"$diffPercent"_"$testName"_"$id"
if [ "$PARALLEL" ]
then
	resultfolder=$resultfolder"_parallel"
fi		
while [[ -d $resultfolder ]]
do
	id=$((id+1))
	resultfolder=~/.KoPeMe/repetitionEvolution_"$basesize"_"$redirect"_"$diffPercent"_"$testName"_"$id"
	if [ "$PARALLEL" ]
	then
		resultfolder=$resultfolder"_parallel"
	fi		
done

rm temp/* -rf

echo "Writing to $resultfolder"
mkdir -p $resultfolder

function runRepetition(){
	repetitions=$1
	vms=$2
	testcases=$3
	basesize=$5
    diffAbsolute=$4
 
	echo "Executing $repetitions repetitions $vms VMs Diff: $diffPercent"
	executions=$(echo "10000000/$repetitions" | bc)
	echo "Setting executions to $executions, basesize=$basesize"
	sed -i "s/EXECUTIONS = [0-9]\+/EXECUTIONS = $executions/g" ../src/test/java/de/precision/Constants.java
	sed -i "s/REPETITIONS = [0-9]\+/REPETITIONS = $repetitions/g" ../src/test/java/de/precision/Constants.java
	export repetitions=$repetitions
	for testcase in "${testcases[@]}"
	do
		durations=""
		echo "Executing $testcase"
		for (( i=1; i<=$vms; i++ ))
		do
			start=$(date +%s%N)
			echo -n "Executing $i $testcase "
			
			if [ "$PARALLEL" ]
			then
				runVersionMeasurementParallel
			else
				runVersionMeasurement
			fi
			
			end=$(date +%s%N)
			duration=$(echo "($end-$start)/1000000" | bc)
			durations="$durations $duration"
			average=$(echo $durations | getSum | awk '{print $2/1000}' | tr "," ".")
			remaining=$(echo "scale=2; $average*($vms-$i)/60" | bc -l)
			echo " Remaining: $remaining minutes"
		done
	done
	
	mkdir $resultfolder/precision_$repetitions
	mv $resultfolder/result_* $resultfolder/precision_$repetitions
	mv *.txt $resultfolder/precision_$repetitions
	tar -I pxz -cf $resultfolder/precision_$repetitions.tar -C $resultfolder/precision_$repetitions .
}

#runRepetition 100000 $vms $tests $diffPercent $basesize
#runRepetition 75000 $vms $tests $diffPercent $basesize
#runRepetition 50000 $vms $tests $diffPercent $basesize
#runRepetition 25000 $vms $tests $diffPercent $basesize
runRepetition 10000 $vms $tests $diffPercent $basesize
#runRepetition 7500 $vms $tests $diffPercent $basesize
#runRepetition 5000 $vms $tests $diffPercent $basesize
#runRepetition 2500 $vms $tests $diffPercent $basesize
#runRepetition 1000 $vms $tests $diffPercent $basesize
#runRepetition 750 $vms $tests $diffPercent $basesize
#runRepetition 500 $vms $tests $diffPercent $basesize
#runRepetition 250 $vms $tests $diffPercent $basesize
#runRepetition 100 $vms $tests $diffPercent $basesize
#runRepetition 10 $vms $tests $diffPercent $basesize
#runRepetition 1 $vms $tests $diffPercent $basesize

#for repetitions in {10..100..10}
#do
#	runRepetition $repetitions $vms $tests $diffPercent $basesize
#done

../gradlew -p ../precision-analysis clean fatJar
java -Xmx14g \
	-cp ../precision-analysis/build/libs/precision-analysis-all-2.13.jar \
	de.precision.analysis.repetitions.GeneratePrecisionPlot -data $resultfolder

cat $resultfolder/results/$tests.csv | grep " $VMS "

#folder_test="${tests##*.}"
#folder="$folder_test"_"$basesize"
#mkdir ~/.KoPeMe/$folder
#mv ~/.KoPeMe/precision_* ~/.KoPeMe/$folder

