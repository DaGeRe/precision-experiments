#!/bin/bash

val1=( de.addtest.sequence.Sequence1Test )

max=50

for repititions in {1..50}
do
	echo "Executing $repititions times"
	export sequences=$repititions
	for testcase in "${val1[@]}"
	do
        testcase2=$(echo $testcase | sed "s/1/2/")
        echo "Executing $testcase and $testcase2"
        for (( i=1; i<=$max; i++ ))
        do
                  gradle clean test --tests $testcase
                  gradle clean test --tests $testcase2
        done
	done
	echo "Creating folder  ~/.KoPeMe/default/sequence_$repititions"
	mkdir ~/.KoPeMe/default/sequence_$repititions
    mv ~/.KoPeMe/default/de.addtest.sequence* ~/.KoPeMe/default/sequence_$repititions
    tar -I pxz -cf ~/.KoPeMe/results_$repititions.tar  -C ~/.KoPeMe/default/ .
done

