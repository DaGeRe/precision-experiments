#!/bin/bash

source functions.sh

getTests "$@"
getGlobalParameters 

setExecutions 10000000

echo "Running with $vms VMs"

testName=${tests##*.}
id=1
resultfolder=~/.KoPeMe/cov_"$redirect"_"$tests"_"$id"
while [[ -d $resultfolder ]]
do
   id=$((id+1))
   resultfolder=~/.KoPeMe/cov_"$redirect"_"$tests"_"$id"
done

echo "Writing to $resultfolder"
mkdir $resultfolder

workload=300
export repetitions=1

durations=""
for (( i=1; i<=$vms; i++ ))
do
	start=$(date +%s%N)
	echo -n "Executing $tests $i "
	export workloadsize=$workload
	while true; do echo "iteration" >> cpu.txt; cat /proc/cpuinfo | grep "MHz" | awk '{print $4}' >> cpu.txt; sleep 1; done &
	../gradlew -p .. clean test --tests $tests > execution_$i.txt
	kill $!
	sensors | grep Core | awk '{print $3}' | tr -d "+°C" | getSum | awk '{print $2}' >> execution_$i.txt
	cat cpu.txt >> execution_$i.txt
	rm cpu.txt
	mv ~/.KoPeMe/de.peass/precision-experiment/$tests $resultfolder/result_$i
	mv execution_$i.txt $resultfolder/
  
	end=$(date +%s%N)
	duration=$(echo "($end-$start)/1000000" | bc)
	durations="$durations $duration"
	average=$(echo $durations | getSum | awk '{print $2/1000}')
	remaining=$(echo "scale=2; $average*($vms-$i)/60" | bc -l)
	echo " Remaining: $remaining minutes"
done

(cd $resultfolder && tar -I pxz -cf logs_$tests.tar execution_*.txt && rm execution*)

../gradlew -p ../precision-analysis/ clean fatJar
java -cp ../precision-analysis/build/libs/precision-analysis-all-2.13.jar \
	de.precision.processing.GenerateCoVPlots \
	$resultfolder 5000

java -cp ../precision-analysis/build/libs/precision-analysis-all-2.13.jar \
	de.precision.analysis.IterationEvolution.GetIterationEvolution \
	$resultfolder/aggregated

(cd $resultfolder && tar -I pxz -cf results_$tests.tar result_* && rm result_* -r)
