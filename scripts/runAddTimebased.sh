#!/bin/bash

max=20

for (( i=1; i<=$max; i++ ))
do
	        echo $i
		gradle clean test -p .. --tests de.precission.add.AddTime1 &> time_1_$i.txt
	        gradle clean test -p .. --tests de.precission.add.AddTime2 &> time_2_$i.txt
done

