#!/bin/bash

val1=( de.precision.add.AddSmall1 de.precision.add.Add1BigTest de.precision.type.TypeTest1 de.precision.sync.SyncAddSmall1 de.precision.ram.RAM1Test de.precision.assertType.TypeAssertTest1 de.precision.sysout.Sysout1Test)

vms=30

echo "Executing 1 time"
export repetitions=1
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
echo "Creating folder  ~/.KoPeMe/default/repetition_$repititions"
mkdir ~/.KoPeMe/default/repetition_$repititions
mv ~/.KoPeMe/default/de.* ~/.KoPeMe/default/repetition_$repititions
tar -I pxz -cf ~/.KoPeMe/results_$repititions.tar  -C ~/.KoPeMe/default/ .

tar -I pxz -cf ~/.KoPeMe/results_full_"$vms"_"$repititions".tar  -C ~/.KoPeMe/default/ .

