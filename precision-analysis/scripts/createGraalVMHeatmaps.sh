
function getHeatmapData {
	index=$1
	outname=$2
	file=$3
	cat $file \
		| awk '{print $2" "$3" "$'$index'}' \
		| sort -k 1 -k 2 -n \
		| awk -f $start/addblanks.awk \
		> $outname
}

function createHistograms {
	start=$1
	folder=$2
	cd $folder
	for file in histogram_*
	do
		echo "Handling: $file"
		cd $start/$folder
		cd $file
		
		if [ ! -f predecessor.csv ]
		then
			firstFileName=$(echo $file | tr -d "histogram_")
			mv $firstFileName".csv" predecessor.csv
			file2=$(ls | grep -v predecessor.csv)
			mv $file2 current.csv
		
			gnuplot -c $start/plotGraalVMHistogram.plt
		fi
	done
}

if [ $# -eq 0 ]
then
	echo "Arguments missing"
	exit 1
fi

start=$(pwd)

base=results_graalvm

echo "Basefolder: $base"

mkdir -p $base

cd $1

for file in *csv
do
	cd $1
	
	echo "Plotting $file"
	
	foldername=$start/$base/${file%.*}
	mkdir $foldername
		
	if [[ $file == unequal_* ]]
	then
		getHeatmapData 9 $foldername/100k_mean.csv $file
		getHeatmapData 13 $foldername/100k_ttest.csv $file
		getHeatmapData 21 $foldername/100k_confidence.csv $file
		getHeatmapData 25 $foldername/100k_mann.csv $file
	else
		getHeatmapData 10 $foldername/100k_mean.csv $file
		getHeatmapData 14 $foldername/100k_ttest.csv $file
		getHeatmapData 22 $foldername/100k_confidence.csv $file
		getHeatmapData 26 $foldername/100k_mann.csv $file
	fi

	cd $foldername

	gnuplot -c $start/plotGraalVMHeatmap.plt
	mv result_meanTTest.pdf "meanTTest.pdf"
	mv result_other.pdf "other.pdf"

	cd $start
done 

createHistograms $start $1
