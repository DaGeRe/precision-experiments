#!/bin/bash

max=100

export repetitions=1000
for (( i=1; i<=$max; i++ ))
do
	echo $i
	gradle -p .. --no-daemon clean test --tests de.precision.empty.EmptyTest1 &> empty_1_$i.txt
	gradle -p .. --no-daemon clean test --tests de.precision.empty.EmptyTest2 &> empty_2_$i.txt
done
