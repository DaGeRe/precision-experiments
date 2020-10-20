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
        echo "Please provide the results-folder you want to analyze (folder should contain .csv-files of measurement)"
        exit 1
fi

if [ $# -gt 1 ]
then
	repetitions=$2
else
	repetitions=100000
fi


start=$(pwd)

test=$(echo $1 | awk -F'/' '{print $(NF-1)}')
base=$(basename $1)

base=$base"_"$test

type=$(echo $base | awk -F'_' '{print $2}')

echo $base

mkdir -p $base

cd $1

getHeatmapData 9 $start/$base/mean.csv $repetitions
getHeatmapData 13 $start/$base/ttest.csv $repetitions
getHeatmapData 17 $start/$base/bimodalttest.csv $repetitions
getHeatmapData 21 $start/$base/confidence.csv $repetitions
getHeatmapData 25 $start/$base/mann.csv $repetitions

cd $start/$base

echo "Plotting... "
gnuplot -c $start/plotHeatmaps.plt
mv result_meanTTest.pdf $test"_"$type"_meanTTest.pdf"
mv result_other.pdf $test"_"$type"_other.pdf"
