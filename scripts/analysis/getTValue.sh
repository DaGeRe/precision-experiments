echo "Das ist das Shell-Skript, nicht die Funktion getTValue!"
function createCSVs {
  versions=$(cat $1 | grep "<gitversion" | sort | uniq | awk -F '>' '{print $2}' | awk -F '<' '{print $1}')
  for version in $versions
  do
    cat $1 | grep $version -B 5 | grep "<value" | tr -d "<value/>" > $version.csv
  done
  versioncount=$(echo $versions | awk '{print NF}')
  echo $versioncount
  if [[ $versioncount == 2 ]]
  then
    file1=$(echo $versions | awk '{print $1}')
    file2=$(echo $versions | awk '{print $2}')
    echo $file1
    printTValue "$file1.csv" "$file2.csv"
  fi
}

function createSimpleCSVs {
	versions=$(grep "<result date" $1 | awk -F '"' '{print $2}')
	i=0
	for version in $versions
	do
	      cat $1 | xmlstarlet sel -t -v "//result[@date="$version"]/fulldata" > $i.csv
	      i=$(echo $i'+1' | bc)
        done
}



function getRepetitionData {
  repetitions=$(ls | grep repetition_ | tr -d "repetition_" | sort -n)
  files=$(ls repetition_1/)
  rm data_*
  rm dist_*
  for file in $files
  do
	echo $file
	for repFolder in $repetitions
  	do 
		 grep -A 1 "<result" repetition_$repFolder/$file/add.xml \
	  	| grep "<value" \
	  	| tr -d "<value/>" | tr "\n" ";" | tr -d " " >> dist_$file.csv
		echo "" >> dist_$file.csv
		grep -A 1 "<result" repetition_$repFolder/$file/add.xml \
		| grep "<value" \
		| tr -d "<value/>" \
		| sort -n \
		| awk 'NR==1 {max=0; min=100000} NR>0 {sum+=$1;if (min>$1) min=$1; if (max<$1) max=$1;all[NR]=$0} END {print sum/NR";"min";"max";"all[int(NR*0.95-0.5)]";"all[int(NR*0.05+0.5)]}' >> data_$file.csv
	done
		  #| awk -F, '{ sum+=$i; sq+=$i^2 } END {printf "%.1f %.1f %d\n", sum/n, sqrt((sq-sum^2/n)/(n-1)), n }'
  done
}

function getRepetitionMeans {
  repetitions=$(ls | grep repetition_ | sort -n)
  files=$(ls repetition_1/)
  for file in $files
  do
	echo $file
	for repFolder in $repetitions
  	do 
		 grep -A 1 "<result" $repFolder/$file/add.xml \
	  	| grep "<value" \
	  	| tr -d "<value/>" \
		| awk '{sum+=$1; sq+=$1^2} END {std=sqrt((sq-sum^2/NR)/(NR-1)); print sum/NR";"std";"std/(sum/NR)}' | sed 's/\./,/g' >> $file.csv
	done
		  #| awk -F, '{ sum+=$i; sq+=$i^2 } END {printf "%.1f %.1f %d\n", sum/n, sqrt((sq-sum^2/n)/(n-1)), n }'
  done
}

function printAvgs {
        mean1=$(awk -F ';' '{print $1}' $1 | awk '{sum += $1; square += $1^2} END {print sum / NR}')
        mean2=$(awk -F ';' '{print $1}' $2 | awk '{sum += $1; square += $1^2} END {print sum / NR}')
        deviation1=$(awk -F ';' '{print $1}' $1 | awk '{sum += $1; square += $1^2} END {print sqrt(square / NR - (sum/NR)^2)}')
        deviation2=$(awk -F ';' '{print $1}' $2 | awk '{sum += $1; square += $1^2} END {print sqrt(square / NR - (sum/NR)^2)}')
        echo "$mean1;$mean2;$deviation1;$deviation2" >> results.csv
        echo $mean1 $mean2
        echo $deviation1 $deviation2
}

function printTValue {
        mean1=$(awk -vOFMT=%.10g -F ';' '{print $1}' $1 | awk -vOFMT=%.10g '{sum += $1; square += $1^2} END {print sum / NR}')
        mean2=$(awk -vOFMT=%.10g -F ';' '{print $1}' $2 | awk -vOFMT=%.10g '{sum += $1; square += $1^2} END {print sum / NR}')
        deviation1=$(awk -vOFMT=%.10g -F ';' '{print $1}' $1 | awk -vOFMT=%.10g '{sum += $1; square += $1^2} END {print sqrt(square / NR - (sum/NR)^2)}')
        deviation2=$(awk -vOFMT=%.10g -F ';' '{print $1}' $2 | awk -vOFMT=%.10g '{sum += $1; square += $1^2} END {print sqrt(square / NR - (sum/NR)^2)}')
	size=$(cat $1 | wc -l)	
	sizefactor=$(echo "sqrt ("$size*$size/"("$size+$size"))" | bc -l)
	weighteddeviation=$(echo "sqrt(("$deviation1*$deviation1"/2)+("$deviation2*$deviation2"/2))" | bc -l)
	tvalue=$(echo "$sizefactor*($mean1-$mean2)/$weighteddeviation" | bc -l)
	echo "Means: $mean1 $mean2 Deviations: $deviation1 $deviation2 Sizefactor: $sizefactor Weighted: $weighteddeviation"
	echo "T-Value: $tvalue Freiheitsgrade: "$(echo $size*2-2 | bc -l)
}

function printMean {
        mean1=$(awk -F ';' '{print $1}' $1 | awk '{sum += $1; square += $1^2} END {print sum / NR}')
        deviation1=$(awk -F ';' '{print $1}' $1 | awk '{sum += $1; square += $1^2} END {print sqrt(square / NR - (sum/NR)^2)}')
	size=$(cat $1 | wc -l)
	echo $mean1 $deviation1 $size
}

function getStatisticsXML {
  xmlstarlet ed -u "//fulldata/value" -x "concat(.,',')" $1 \
  | xmlstarlet sel -B -t -v "//fulldata" -n \
  | awk -F, '{ n=NF-1; sum=sq=0; 
      for(i=1000;i<=n;i++) { sum+=$i; sq+=$i^2 }
      printf "%.1f %.1f %d\n", sum/n, sqrt((sq-sum^2/n)/(n-1)), n }'
}

function getTValue {
  if [ $# -lt 3 ]; then
	  echo "3 Argumente müssen übergeben werden!"
  fi
  folder=$(mktemp -d) 
  xmlstarlet ed -u "//fulldata/value" -x "concat(.,',')" $1 \
  | xmlstarlet sel -B -t -v "//fulldata" -n \
  | awk -F, '{ n=NF-1; sum=sq=0; 
      for(i='$3';i<=n;i++) { sum+=$i; sq+=$i^2 }
      printf "%.1f %.1f %d\n", sum/n, sqrt((sq-sum^2/n)/(n-1)), n }' &> $folder/1.csv
  xmlstarlet ed -u "//fulldata/value" -x "concat(.,',')" $2 \
  | xmlstarlet sel -B -t -v "//fulldata" -n \
  | awk -F, '{ n=NF-1; sum=sq=0; 
      for(i='$3';i<=n;i++) { sum+=$i; sq+=$i^2 }
      printf "%.1f %.1f %d\n", sum/n, sqrt((sq-sum^2/n)/(n-1)), n }' &> $folder/2.csv
  printTValue $folder/1.csv $folder/2.csv
  echo $folder
}


