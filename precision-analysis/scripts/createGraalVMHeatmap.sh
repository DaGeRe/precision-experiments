
function getHeatmapData {
	index=$1
	outname=$2
	repetitions=$3
	cat unkownClazz.csv \
		| awk '{print $2" "$3" "$'$index'}' \
		| sort -k 1 -k 2 -n \
		| awk -f $start/addblanks.awk \
		> $outname
}

if [ $# -eq 0 ]
then
	echo "Arguments missing"
	exit 1
fi

start=$(pwd)

test=$(echo $1 | awk -F'/' '{print $(NF-1)}')
base=$(basename $1)

base=$base"_"$test

type=$(echo $base | awk -F'_' '{print $2}')

echo "Basefolder: $base Test: $test"

mkdir -p $base

cd $1

getHeatmapData 13 $start/$base/unkownClazz.csv $repetitions

getHeatmapData 9 $start/$base/100k_mean.csv $size
getHeatmapData 13 $start/$base/100k_ttest.csv $size
getHeatmapData 21 $start/$base/100k_confidence.csv $size
getHeatmapData 25 $start/$base/100k_mann.csv $size

cd $start/$base

gnuplot -c $start/plotGraalVMHeatmap.plt
mv result_meanTTest.pdf $test"_"$type"_meanTTest.pdf"
mv result_other.pdf $test"_"$type"_other.pdf"

