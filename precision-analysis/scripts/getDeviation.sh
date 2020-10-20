function getSum {
   awk -M '{sum += $1; square += $1^2} END {print sqrt(square / NR - (sum/NR)^2)" "sum/NR" "NR}'
}

for folder in $(ls | grep "Test")
do
	cd $folder
for file in $(ls | grep precision | grep -v .tar)
do
	echo $file
	fullsize=$(cat $file/result_300_1/add.xml | grep executionTimes | tr -d "<executionTimes/>")
	size=$(echo $fullsize/2 | bc)
	if [ -f $file/result_300_1/kopeme*tmp ]
	then
		for mfile in $(ls $file | grep result_300_)
		do
			cat $file/$mfile/kopeme*tmp | grep "=" | tail -n $size | awk -F'=' '{print $2}' | getSum | awk '{print $2}'
		done | getSum 
		for mfile in $(ls $file | grep result_301_)
		do
			cat $file/$mfile/kopeme*tmp | grep "=" | tail -n $size | awk -F'=' '{print $2}' | getSum | awk '{print $2}'
		done | getSum 
	else
		for mfile in $(ls $file | grep result_300_)
		do
			cat $file/$mfile/*xml | grep "value start" | awk -F'[<>]' '{print $3}' | tail -n $size | getSum | awk '{print $2}'
		done | getSum 
		for mfile in $(ls $file | grep result_301_)
		do
			cat $file/$mfile/*xml | grep "value start" | awk -F'[<>]' '{print $3}' | tail -n $size | getSum | awk '{print $2}'
		done | getSum 
	fi
done
	cd ..
done

#cat $file/result_300_*/add.xml | grep "<value>" | tr -d "<value/>" | getSum | awk '{print $1/$2}'
#cat $file/result_301_*/add.xml | grep "<value>" | tr -d "<value/>" | getSum | awk '{print $1/$2}'
