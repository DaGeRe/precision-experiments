function getSum {
	  awk '{sum += $1; square += $1^2} END {print sqrt(square / NR - (sum/NR)^2)" "sum/NR" "NR}'
}


for file in $(ls | grep -v .sh)
do
	echo $file
	rm $file/histogram.csv
	for result in $file/aggregated/result*
	do
		index=$(echo $result | awk -F'_' '{print $3}')
		cat $result | getSum | tr "\n" " " >> $file/histogram.csv
		logfile="$file/execution_$index.txt"
		if [ -f $logfile ]
		then
			if grep -Fxq "iteration" $logfile
			then
				cat $logfile \
				| grep "iteration" -A 8 \
				| grep -v iteration \
				| getSum >> $file/histogram.csv
			else
				echo >> $file/histogram.csv
			fi
		else
			echo >> $file/histogram.csv 
		fi
	done
done
