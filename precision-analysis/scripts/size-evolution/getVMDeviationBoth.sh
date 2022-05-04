function getSum {
	  awk '{sum += $1; square += $1^2} END {print sqrt(square / NR - (sum/NR)^2)" "sum/NR" "NR}'
}

for size in $(cat AddTest/vmdeviation_evolution.csv | grep -v "#" | awk '{print $1}')
do
	echo -n "$size "
	for testcase in AddTest RAMTest SysoutTest
	do
		cat AddTest/vmdeviation_evolution.csv | grep "^$size "
	done | awk '{print $2}' | getSum | awk '{print $2}' | tr "\n" " "
	
	for testcase in AddTest RAMTest SysoutTest
	do
		cat $testcase/vmdeviation_evolution_absolute.csv | grep "^$size "
	done | awk '{print $2}' | getSum | awk '{print $2}' 
done > evolution.csv

gnuplot -c 'plotBothMeans.plt'
