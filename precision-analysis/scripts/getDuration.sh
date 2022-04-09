function getSum {
	  awk '{sum += $1; square += $1^2} END {print sqrt(square / NR - (sum/NR)^2)" "sum/NR" "NR}'
  }

function getDuration {
   file=$1
   for measurementfile in $(ls $file | grep result_)
   do
	if [ -f $file/$measurementfile/kopeme*tmp ]
	then
		start=$(cat $file/$measurementfile/kopeme*.tmp | head -n 2 | tail -n 1)
		end=$(cat $file/$measurementfile/kopeme*.tmp | tail -n 2 | head -n 1)
		echo "($end-$start)/1000" | bc -l
	else
		start=$(cat $file/$measurementfile/*.xml | grep "value start" | head -n 1 | awk -F'\"' '{print $2}')
		end=$(cat $file/$measurementfile/*.xml | grep "value start" | tail -n 1 | awk -F'\"' '{print $2}')
		echo "($end-$start)/1000" | bc -l
	fi
   done 
}

if [ $# -lt 1 ]
then
	echo "Argument missing, please provide folder where you expect precision-analysis results data (repo-measurement-configuration/basic-parameter-comparison)"
	exit 1
fi

start=$(pwd)

mkdir -p $start/durations/

cd $1

for workloadFolder in $(ls | grep Test | grep -v ".txt")
do
	cd $workloadFolder
	echo "Reading from $workloadFolder"
	for file in $(ls | grep precision | grep -v ".tar" | grep -v ".txt" | grep -v ".csv")
	do
		echo $file | tr -d "precision_\n"
		echo -n " "
		duration=$(getDuration $file | getSum)
		echo $duration
	done > $start/durationData/$workloadFolder.csv
	cd ..
done

cd $start/durations
gnuplot -c plotDurations.plt
