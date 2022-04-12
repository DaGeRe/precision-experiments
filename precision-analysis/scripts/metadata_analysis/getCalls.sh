function getProjectCalls {
	project=$1
	
	projectFolder=$1/views_$1
	if [ -d $projectFolder ]
	then
		cd $projectFolder
		for file in view_*; 
		do
			testcases=$(ls $file | grep -v "diff")
			for testcase in $testcases
			do
				testmethods=$(ls $file/$testcase/)
				for testmethod in $testmethods
				do
					#echo $file/$testcase/$testmethod/
					expandedFiles=( $file/$testcase/$testmethod/*_method_expanded )
					if [ -f ${expandedFiles[0]} ]
					then
						calls=$(cat $file/$testcase/$testmethod/*_method_expanded | grep -v "#get" | grep -v "#is" | grep -v "#set" | grep -v '#access\$' | wc -l)
						methodCompressedCount=$(cat $file/$testcase/$testmethod/*_method | grep -v "#get" | grep -v "#is" | grep -v "#set" | grep -v '#access\$' | wc -l)
						methodCompressedLines=$(cd $file/$testcase/$testmethod/; \
						        cat *_nocomment | tr -d '\000' \
						        | grep -a -v "}" \
						        | grep -v "return" \
						        | grep -v "break" \
						        | grep -v "//" \
						        | grep -v "}" \
						        | grep -v "try {" \
						        | grep -v "@Override" \
						        | grep -v "@SuppressWarning" \
						        | grep -v "case " \
						        | grep -v -e '^[[:space:]]*$' \
						        | egrep -v "^[ ]*\*[ ]*" \
						        | egrep -v "^[ ]*/\*" \
						        | egrep -v "[ ]*\([0-9]*\)[ ]*$" \
						        | grep -v "#get" | grep -v "#is" | grep -v "#set" | grep -v ".get" | grep -v ".is" | grep -v ".set" | grep -v '#access\$' \
						        | grep -v "super(" | grep -v "@Test" | grep -v "@PerformanceTest" | grep -v "@de.dagere.kopeme.annotations.PerformanceTest" | grep -v "continue" \
						        | egrep -v "^[ ]*int [a-zA-Z_0-9]*;" | egrep -v "^[ ]*int [a-zA-Z_0-9]*[ ]*=[ ]*[-0-9]+;" | egrep -v "^[ ]*final int [a-zA-Z_0-9]*[ ]*=[ ]*[-0-9]+;" \
						        | egrep -v "^[ ]*long [a-zA-Z_0-9]*;" | egrep -v "^[ ]*long [a-zA-Z_0-9]*[ ]*=[ ]*[-0-9]+;" | egrep -v "^[ ]*final long [a-zA-Z_0-9]*[ ]*=[ ]*[-0-9]+;" \
						        | egrep -v "^[ ]*byte [a-zA-Z_0-9]*;" | egrep -v "^[ ]*byte [a-zA-Z_0-9]*[ ]*=[ ]*[-0-9]+;" | egrep -v "^[ ]*final byte [a-zA-Z_0-9]*[ ]*=[ ]*[-0-9]+;" \
						        | egrep -v "^[ ]*char [a-zA-Z_0-9]*;" \
						        | egrep -v "^[ ]*float [a-zA-Z_0-9]*;" | egrep -v "^[ ]*float [a-zA-Z_]*[ ]*=[ ]*[0-9]+;" \
						        | egrep -v "^[ ]*double [a-zA-Z_]*;" | egrep -v "^[ ]*double [a-zA-Z_]*[ ]*=[ ]*[-0-9.]+;" | egrep -v "^[ ]*final double [a-zA-Z_0-9]*[ ]*=[ ]*[-0-9.]+;" | egrep -v "^[ ]*double [a-zA-Z_0-9]*[ ]*=[ ]*Double.MAX_VALUE+;" \
						        | egrep -v "^[ ]*boolean [a-zA-Z_0-9]*;" | egrep -v "^[ ]*boolean [a-zA-Z_0-9]*[ ]*=[ ]*true;"| egrep -v "^[ ]*boolean [a-zA-Z_0-9]*[ ]*=[ ]*false;" | egrep -v "^[ ]*final boolean [a-zA-Z_0-9]*[ ]*=[ ]*true;"| egrep -v "^[ ]*final boolean [a-zA-Z_0-9]*[ ]*=[ ]*false;" \
						        | egrep -v "^[ ]*String [a-zA-Z_0-9]*;" | egrep -v "^[ ]*final String [a-zA-Z_0-9]*[ ]*=[ ]*\"[a-zA-Z_0-9 /]*\";" \
						        | egrep -v "^[ ]*[a-zA-Z_0-9]* [a-zA-Z_0-9]*[ ]*;" | egrep -v "^[ ]*[a-zA-Z_0-9]* [a-zA-Z_0-9]*[ ]*=[ ]*null;" \
						        | wc -l)

						#echo $calls" "$methodCompressedCount" "$methodCompressedLines
						averageSize=$(echo "("$methodCompressedLines"-"$methodCompressedCount")/"$methodCompressedCount | bc -l)
						#echo "Average size: $averageSize $methodCompressedLines/$methodCompressedCount"
						echo $calls" "$averageSize
					fi
				done
			done
		done
	fi
}

start=$(pwd)
for file in $(ls | grep ".tar" | grep -v "der_" | grep -v "peass_metadata.tar")
do
	echo "Extracting $file"
	tar --one-top-level -xf $file
	folder=${file%.tar}
	echo "Analyzing $folder"
	getProjectCalls $folder > $folder.csv
	cd $start
	rm $folder -r
done
cat *.csv | awk '{sum+=$1; sum2+=$2} END {print sum/NR" "sum2/NR}'

cat *.csv | grep -v "view_" | sort > all.csv
gnuplot -c plotHistogram.plt

echo "Median Call Count"
cat all.csv | awk '{print $1}' | sort -n | awk '{ count[NR] = $1; } END { if (NR % 2) { print count[(NR + 1) / 2]; } else { print (count[(NR / 2)] + count[(NR / 2) + 1]) / 2.0; } }'

echo "Median Method Length"
cat all.csv | awk '{print $2}' | sort -n | awk '{ count[NR] = $1; } END { if (NR % 2) { print count[(NR + 1) / 2]; } else { print (count[(NR / 2)] + count[(NR / 2) + 1]) / 2.0; } }'
