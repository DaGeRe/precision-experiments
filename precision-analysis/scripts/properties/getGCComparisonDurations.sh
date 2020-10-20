function getSum {
   awk '{sum += $1; square += $1^2} END {print sqrt(square / NR - (sum/NR)^2)" "sum/NR" "NR}'
}

if [ $# -eq 0 ]
then
	echo "Arguments missing: please pass folder with cov-test-results (either with or without GC activated) - measurements should be extracted (to get durations)"
	exit 1
fi

cd $1

for file in $(ls | grep -v .tar | grep -v .sh | grep -v alt)
do
	echo -n "$file "
	cd $file
	start=$(cat result_1/*.xml | grep "<result" | awk -F'"' '{print $(NF-1)}')
	lastFileIndex=$(ls | grep result | awk -F'_' '{print $2}' | sort -n | tail -n 1)
	echo $lastFileIndex
	end=$(tail -n 2 "result_"$lastFileIndex/kopeme* | grep -v "=")
	diff=$(echo "($end-$start)/1000" | bc -l)
	diffInH=$(echo "$diff/3600.0/$lastFileIndex" | bc -l)
	echo $start" "$end" "$diff" "$diffInH" h"
	cd ..
done

for file in $(ls | grep -v .tar | grep -v .sh | grep -v alt)
do
       	echo $file
       	cat $file/aggregated/steady_state.csv | awk '{print $3}' | getSum
done
