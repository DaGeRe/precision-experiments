#!/bin/bash

val1=( de.sequence.add.Sequence1AddTest de.sequence.add.AddSmall1 de.sequence.file.Sequence1FileTest de.sequence.ram.Sequence1RAMTest )

if [[ -z "$VMS" ]]; then
  vms=30
else
  vms=$VMS
fi

echo "Running with $vms VMs"


export repetitions=1
for testcase in "${val1[@]}"
do
        testcase2=$(echo $testcase | sed "s/1/2/")
        echo "Executing $testcase and $testcase2"
        for (( i=1; i<=$vms; i++ ))
        do
                  gradle clean test --tests $testcase >> execution_$i.txt
                  gradle clean test --tests $testcase2 >> execution_$i.txt
    done
done

echo "Creating folder  ~/.KoPeMe/default/cov_$vms"
mkdir ~/.KoPeMe/default/cov_$vms
mv ~/.KoPeMe/default/de.* ~/.KoPeMe/default/cov_$vms

tar -I pxz -cf ~/.KoPeMe/results_cov_"$vms".tar  -C ~/.KoPeMe/default/ .

