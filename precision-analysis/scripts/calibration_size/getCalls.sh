for file in *.tar
do
	echo "Extracting $file"
	tar -xf $file
	folder=${file%.tar}
	echo "Analyzing $folder"
	mkdir $folder
	mv views_$folder $folder
	mv properties_$folder $folder
	mv *.json $folder
	./getMethodCalls.sh $folder > $folder.csv
	rm $folder -r
        cat $folder.csv | awk '{print $1*$2}' | sort -n | awk -f median.awk
done
cat *.csv | awk '{sum+=$1; sum2+=$2} END {print sum/NR" "sum2/NR}'
