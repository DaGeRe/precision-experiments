
function getHeatmapData {
	index=$1
	outname=$2
	repetitions=$3
	cat de.precision.*.csv | grep "^$repetitions " \
		| awk '{print $2" "$3" "$'$index'}' \
		| sort -k 1 -k 2 -n \
		| awk -f $start/addblanks.awk \
		> $outname
}

if [ $# -lt 3 ]
then
	echo "Arguments missing, please specify 3 folders (sequentiel, parallel, with noise)"
	exit 1
fi

start=$(pwd)

test=$(echo $1 | awk -F'/' '{print $(NF-1)}')
base=$(basename $1)

base="parallel_"$base"_"$test

type=$(echo $base | awk -F'_' '{print $2}')

echo "Basefolder: $base Test: $test"

mkdir -p $base

cd $1

mkdir -p $start/$base/bimodal/
for repetitions in $(cat de.precision.*.csv | awk '{print $1}' | uniq | grep -v "#")
do
        echo "Creating heatmap for $repetitions repetitions"
	getHeatmapData 13 $start/$base/sequential_$repetitions.csv $repetitions
	getHeatmapData 17 $start/$base/bimodal/sequential_$repetitions.csv $repetitions
done

cd $2
for repetitions in $(cat de.precision.*.csv | awk '{print $1}' | uniq | grep -v "#")
do
        echo "Creating heatmap for $repetitions repetitions"
	getHeatmapData 13 $start/$base/parallel_$repetitions.csv $repetitions
	getHeatmapData 17 $start/$base/bimodal/parallel_$repetitions.csv $repetitions
done

cd $3
for repetitions in $(cat de.precision.*.csv | awk '{print $1}' | uniq | grep -v "#")
do
        echo "Creating heatmap for $repetitions repetitions"
	getHeatmapData 13 $start/$base/noise_$repetitions.csv $repetitions
	getHeatmapData 17 $start/$base/bimodal/noise_$repetitions.csv $repetitions
done

cd $start/$base
gnuplot -c $start/plotParallelHeatmap.plt

mv heatmap_parallel.pdf "$test"_heatmap_parallel.pdf

