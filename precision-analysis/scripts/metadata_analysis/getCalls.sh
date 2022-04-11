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
cat *.csv | awk '{sum+=$1; sum2+=$2; sum3+=$3} END {print sum/NR" "sum2/NR" "sum3/NR}'

echo "Method count median"
cat *.csv | grep -v "view" | sort -n | awk -f median.awk

echo "Method length median"
cat *.csv | grep -v "view" | awk '{print $2}' | sort -n | awk -f median.awk

echo "Tree depth median"
cat *.csv | grep -v "view" | awk '{print $3}' | sort -n | awk -f median.awk
