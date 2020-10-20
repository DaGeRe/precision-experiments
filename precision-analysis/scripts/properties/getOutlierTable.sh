function getSum {
  awk '{sum += $1; square += $1^2} END {print sqrt(square / NR - (sum/NR)^2)" "sum/NR" "NR}'
}

if [ $# -eq 0 ]
then
	echo "Arguments missing: Please pass folder with cov-test-results"
	exit 1
fi

cd $1


for file in $(ls | grep -v .tar)
do
       	if [ -f $file/aggregated/steady_state.csv ]
	then
		echo -n "$file & "
		cat $file/aggregated/steady_state.csv | awk '{print $3}' | getSum | awk '{print $2" & "$1" \\\\"}' | tr "." "," | numfmt --field=1,3 --grouping
	fi
done
