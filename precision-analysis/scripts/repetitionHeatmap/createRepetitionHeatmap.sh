
function getHeatmapData {
	index=$1
	outname=$2
	repetitions=$3
	cat de.precision.*.csv | grep "^$repetitions " \
		| awk '{print $2" "$3" "$'$index'}' \
		| sort -k 1 -k 2 -n \
		| awk -f $start/../addblanks.awk \
		> $outname
}

if [ $# -eq 0 ]
then
	echo "Arguments missing"
	exit 1
fi

if [ $# -gt 1 ]
then
	size=$2
else
	size=100000
fi

start=$(pwd)

test=$(echo $1 | awk -F'/' '{print $(NF-1)}')
base=$(basename $1)

base=$base"_"$test

type=$(echo $base | awk -F'_' '{print $2}')

echo "Basefolder: $base Test: $test"

mkdir -p $base

cd $1

mkdir -p $start/$base/bimodal/
for repetitions in $(cat de.precision.*.csv | awk '{print $1}' | uniq | grep -v "#")
do
        echo "Creating heatmap for $repetitions repetitions"
	getHeatmapData 13 $start/$base/$repetitions.csv $repetitions
	getHeatmapData 17 $start/$base/bimodal/$repetitions.csv $repetitions
done

getHeatmapData 9 $start/$base/100k_mean.csv $size
getHeatmapData 21 $start/$base/100k_confidence.csv $size
getHeatmapData 25 $start/$base/100k_mann.csv $size

cd $start/$base

if [ ! $size -eq 100000 ]
then
	mv $size.csv 100000.csv
fi

gnuplot -c $start/plotAllHeatmap.plt
mv heatmap_all.pdf $test"_"$type.pdf

gnuplot -c $start/plot100kHeatmap.plt
mv result_meanTTest.pdf $test"_"$type"_meanTTest.pdf"
mv result_other.pdf $test"_"$type"_other.pdf"

cd bimodal
gnuplot -c $start/plotAllHeatmap.plt
mv heatmap_all.pdf $test"_"$type"_bimodal.pdf"
