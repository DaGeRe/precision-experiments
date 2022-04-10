function getSum {
   awk '{sum += $1; square += $1^2} END {print sqrt(square / NR - (sum/NR)^2)" "sum/NR" "NR}'
}

# Use this if you only want to extract the first and last file, to get the overall duration  
function extractWithXMLName {
	file=$1
	xmlName=$2
	
	echo "  Extracting 1 $file $xmlName"
	tar --occurrence=1 -xvf results_de.precision.*.tar result_1/$xmlName.xml
	echo "  Extracting 1000 $file $xmlName"
	tar --occurrence=1 -xvf results_de.precision.*.tar result_1000/$xmlName.xml
	detailFile=$(cat result_1000/$xmlName.xml | grep "fileName" | awk -F'[<>]' '{print $3}')
	tar --occurrence=1 -xvf results_de.precision.*.tar result_1000/$detailFile
}

if [ $# -eq 0 ]
then
	echo "Arguments missing: please pass folder with cov-test-results (either with or without GC activated) - measurements should be extracted (to get durations)"
	exit 1
fi

cd $1

for file in $(ls | grep -v .tar | grep -v .sh | grep -v alt | grep -v ".plt")
do
	echo -n "$file "
	cd $file
	start=$(cat result_1/*.xml | grep "<result" | awk -F'"' '{print $(NF-1)}')
	lastFileIndex=$(ls | grep result | awk -F'_' '{print $2}' | sort -n | tail -n 1)
	end=$(tail -n 2 "result_"$lastFileIndex/kopeme* | grep -v "=")
	diffInSeconds=$(echo "($end-$start)/1000/$lastFileIndex" | bc -l)
	if [ $(echo $diffInSeconds'>'3600 | bc -l) = 1 ]
	then
		printf "%.2f" $(echo "$diffInSeconds/3600.0" | bc -l | tr "." ",")
		echo " h"
	else
		displayableSeconds=$(numfmt --grouping $(echo $diffInSeconds | tr "." "," | sed 's/^00*\|00*$//g'))
		echo $displayableSeconds" s"
	fi
	cd ..
done

#for file in $(ls | grep -v .tar | grep -v .sh | grep -v alt | grep -v ".plt")
#do
#       	echo $file
#       	cat $file/aggregated/steady_state.csv | awk '{print $3}' | getSum
#done
