
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
		
	getHeatmapData 9 $foldername/100k_mean.csv $file
	getHeatmapData 13 $foldername/100k_ttest.csv $file
	getHeatmapData 21 $foldername/100k_confidence.csv $file
	getHeatmapData 25 $foldername/100k_mann.csv $file

	cd $foldername

	gnuplot -c $start/plotGraalVMHeatmap.plt
	mv result_meanTTest.pdf "meanTTest.pdf"
	mv result_other.pdf "other.pdf"

	cd $start
done 
