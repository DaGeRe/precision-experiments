#!/bin/bash

val1=( de.precision.add.Sequence1AddTest de.precision.add.AddSmall1 de.precision.type.TypeTest1 de.precision.ram.Sequence1RAMTest )

vms=30

for repititions in {10..100..20}
do
    echo "Executing $repititions times"
    export repetitions=$repititions
    for testcase in "${val1[@]}"
    do
        testcase2=$(echo $testcase | sed "s/1/2/")
        echo "Executing $testcase and $testcase2"
        for (( i=1; i<=$vms; i++ ))
        do
                  gradle -p .. clean test --tests $testcase >> execution_$i.txt
                  gradle -p .. clean test --tests $testcase2 >> execution_$i.txt
        done
    done
    echo "Creating folder  ~/.KoPeMe/default/sequence_$repititions"
    mkdir ~/.KoPeMe/default/repetition_$repititions
    mv ~/.KoPeMe/default/de.* ~/.KoPeMe/default/repetition_$repititions
    tar -I pxz -cf ~/.KoPeMe/results_$repititions.tar  -C ~/.KoPeMe/default/ .
done

for (( i=1; i<=$max; i++ ))
do
     gradle -p .. clean test --tests de.sequence.BaselineTest >> execution_base_$i.txt
done

tar -I pxz -cf ~/.KoPeMe/results_full_"$vms"_"$Repititions".tar  -C ~/.KoPeMe/default/ .

