set terminal pdf size 6,3
set out 'histogram_'.ARG1.'.pdf'

print ARG1

binwidth=2
set boxwidth binwidth
bin(x,width)=width*floor(x/width) + width/2.0

set style fill solid 0.5

#set xtics 0,2,200
#set xrange [28.1:28.8]

set logscale y

set multiplot layout 1,2 margins 0.1,0.95,.15,.95 spacing 0,0
set xlabel 'Dauer / ms'
set ylabel 'Frequenz'

plot 'histogramData/'.ARG1.'_fast_100000.csv' using (bin($1/1000000,binwidth)):(1.0) smooth freq with boxes title '300 Additionen'

unset ylabel
unset ytics

plot 'histogramData/'.ARG1.'_slow_100000.csv' using (bin($1/1000000,binwidth)):(1.0) smooth freq with boxes title '301 Additionen'

unset multiplot
unset output

set out 'histogram_'.ARG1.'_en.pdf'

set boxwidth binwidth
bin(x,width)=width*floor(x/width) + width/2.0

set ytics

set multiplot layout 1,2 margins 0.1,0.95,.15,.95 spacing 0.10,0
set xlabel 'Duration / ms'
set ylabel 'Occurences'

plot 'histogramData/'.ARG1.'_fast_100000.csv' using (bin($1/1000000,binwidth)):(1.0) smooth freq with boxes title '300 additions'

unset ylabel
#unset ytics

plot 'histogramData/'.ARG1.'_slow_100000.csv' using (bin($1/1000000,binwidth)):(1.0) smooth freq with boxes title '301 additions'

unset multiplot
unset output
