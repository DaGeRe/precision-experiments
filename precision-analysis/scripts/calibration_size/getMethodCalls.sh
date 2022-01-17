cd $1/views_*
for file in view_*; 
do
	testcases=$(ls $file | grep -v "diff")
	for testcase in $testcases
	do
		testmethods=$(ls $file/$testcase/)
		for testmethod in $testmethods
		do
			echo $file/$testcase/$testmethod/
			calls=$(cat $file/$testcase/$testmethod/*_method_expanded | grep -v "get" | wc -l)
			methodCompressedCount=$(cat $file/$testcase/$testmethod/*_method | wc -l)
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
				| egrep -v "^[ ]*\*[ ]*" \
				| egrep -v "^[ ]*/\*" \
				| egrep -v "[ ]*\([0-9]*\)[ ]*$" \
				| wc -l)
			#echo $calls" "$methodCompressedCount" "$methodCompressedLines

			#Every compressed is 2 times in the header, once for the trace list and once for the first line of the method
			averageSize=$(echo "("$methodCompressedLines"- (2*"$methodCompressedCount") )/"$methodCompressedCount | bc -l)
			#echo "Average size: $averageSize $methodCompressedLines/$methodCompressedCount"
			echo $calls" "$averageSize
		done
	done
done
