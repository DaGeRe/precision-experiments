
if [ $# -lt 1 ] 
then
  echo "Es muss eine Eingabedatei übergeben werden."
  exit
fi

if [ ! -z $2 ]
then
  COUNT_AVG=$2
else
  COUNT_AVG=10
fi

cat $1 | tr -d "+°C" | awk '{print $1 ";" $2+$3+$4+$5}' > $1_clean.csv
cat $1_clean.csv | awk -F ';' '{sum+=$2} (NR%'$COUNT_AVG')==0{print $1";"sum/'$COUNT_AVG'";"$2; sum=0;}' > $1_"$COUNT_AVG"_avg.csv


echo "set datafile separator ';'"
echo "plot '"$1"_"$COUNT_AVG"_avg.csv'"
