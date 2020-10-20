binwidth=0.5
set boxwidth binwidth
set style fill solid 0.5

set encoding iso_8859_1
set terminal pdf size 5,2
set out 'result_100kRAMbimodal.pdf'

set xlabel 'Duration/ms'
set ylabel 'H{\344}ufigkeit'
plot 'slow_100000.csv' using (bin($1/1000000,binwidth)):(1.0) smooth freq with boxes, 'fast_100000.csv' using (bin($1/1000000,binwidth)):(1.0) smooth freq with boxes

unset out

