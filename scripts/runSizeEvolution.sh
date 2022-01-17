#!/bin/bash
# Determines the evolution of coefficients of variation

source functions.sh

getTests "$@"
getGlobalParameters 

if [[ ! $repetitions -eq 1 ]] && [[ ! -z "$repetitions" ]]
then
	echo "Warning: Repetitions should not be set or be 1, but are $repetitions"
fi

testName=${tests##*.}
id=1
resultfolder=~/.KoPeMe/sizeEvolution_"$redirect"_"$testName"_"$id"
while [[ -d $resultfolder ]]
do
   id=$((id+1))
   resultfolder=~/.KoPeMe/sizeEvolution_"$redirect"_"$testName"_"$id"
done

echo "Writing to $resultfolder"
mkdir $resultfolder

function runWorkload {
	vms=$1
	workloadI=$2
	testcase=$3
	echo "Executing $3 Workload $2 VMs $1"
	durations=""
	for (( i=1; i<=$vms; i++ ))
    do
                start=$(date +%s%N)
                echo -n "Executing $i"
                export workloadsize=$workloadI
                if [[ "$BUILDTOOL" == "mvn" ]]
                then
                        mvn -f .. clean test -Dtest=$testcase &> "$workloadsize"_$i.txt
                else
                        ../gradlew -p .. clean test --tests $testcase &> "$workloadsize"_$i.txt
                fi
                mv  ~/.KoPeMe/de.peass/precision-experiments/$testcase $resultfolder/wl_"$workloadsize"_$i
                
                end=$(date +%s%N)
				duration=$(echo "($end-$start)/1000000" | bc)
				durations="$durations $duration"
				average=$(echo $durations | getSum | awk '{print $2/1000}')
				remaining=$(echo "scale=2; $average*($vms-$i)/60" | bc -l)
				echo " Remaining: $remaining minutes"
    done
	java -cp ../precision-analysis/build/libs/precision-analysis-all-2.13.jar \
		        de.precision.processing.GetSizeEvolution \
			$resultfolder
	mv $resultfolder"_result" $resultfolder/aggregated_$workloadsize
	(cd $resultfolder && \
		tar -I xz -cf wl_$workloadsize.tar wl_$workloadsize"_"* &&
		rm wl_$workloadsize"_"* -r)
	(cd $resultfolder && \
		files=$(ls | grep aggregated | awk -F_ '{print $2}' | sort -n) && \
		echo $files && \
		for file in $files; do cat aggregated_$file/mean_evolution.csv; done > mean_evolution.csv && \
		for file in $files; do cat aggregated_$file/vmdeviation_evolution.csv; done > vmdeviation_evolution.csv && \
		for file in $files; do cat aggregated_$file/vmdeviation_evolution_absolute.csv; done > vmdeviation_evolution_absolute.csv)

	
}

../gradlew -p ../precision-analysis/ clean fatJar

setExecutions 1000000

runWorkload $vms 1 $tests
runWorkload $vms 10 $tests
runWorkload $vms 100 $tests
runWorkload $vms 1000 $tests
runWorkload $vms 10000 $tests

setExecutions 100000
runWorkload $vms 100000 $tests

setExecutions 10000
runWorkload $vms 1000000 $tests

setExecutions 1000
runWorkload $vms 10000000 $tests

setExecutions 100
runWorkload $vms 25000000 $tests


tar -I pxz -cf $resultfolder.tar -C $resultfolder .
