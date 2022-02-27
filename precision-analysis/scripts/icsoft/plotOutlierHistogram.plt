bin(x,width)=width*floor(x/width) + width/2.0
binwidth=5
set boxwidth binwidth

set encoding iso_8859_1
set terminal pdf size 5,2

set out 'histogram_outliers_en.pdf'
set multiplot layout 1,2 margins 0.125,0.90,.225,.90 spacing 0.025,0

set style fill solid 0.25 # fill style

set xrange [1180:1320]
set xtics 1180,40,1320

set xlabel 'Duration / ms'
set ylabel 'Occurences'
set logscale y

plot 'fast_100000.csv' using (bin($1/1000000,binwidth)):(1.0) smooth freq with boxes title 'Raw Data'

unset ylabel
#unset ytics

set ytics format ""

binwidth=0.1
set boxwidth binwidth

set xrange [1216:1219]
set xtics 1116,1,1219
plot 'fast_100000.csv' using (bin($1/1000000,binwidth)):(1.0) smooth freq with boxes title 'No Outliers'

unset multiplot
unset output

