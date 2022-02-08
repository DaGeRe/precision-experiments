
function getHeatmapData {
	index=$1
	outname=$2
	cat precision.csv \
		| awk '{print $2" "$3" "$'$index'}' \
		| sort -k 1 -k 2 -n \
		| awk -f $start/addblanks.awk \
		> $outname
}

if [ $# -lt 1 ]
then
	echo "Arguments missing, please specify one folder with Peass analysis results"
	exit 1
fi

start=$(pwd)

test=$(echo $1 | awk -F'/' '{print $(NF-1)}')

base="peass_"$test

type=$(echo $base | awk -F'_' '{print $2}')

echo "Basefolder: $base Test: $test"

mkdir -p $base

cd $1

mkdir -p $start/$base/

getHeatmapData 9 $start/$base/peassExecution_mean.csv
getHeatmapData 13 $start/$base/peassExecution_ttest.csv
getHeatmapData 21 $start/$base/peassExecution_confidence.csv
getHeatmapData 25 $start/$base/peassExecution_mannWhitney.csv

cd $start/$base
gnuplot -c $start/plotPeassHeatmap.plt


