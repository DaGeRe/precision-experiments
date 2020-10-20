set terminal pdf size 6,3
set out 'result.pdf'

binwidth=0.25
set boxwidth binwidth
bin(x,width)=width*floor(x/width) + width/2.0
set xrange [1180:1230]
set style fill solid 0.5
set multiplot layout 1,2
set xlabel 'Dauer / ms'
set ylabel 'Frequenz'
set logscale y 2
plot 'fast_100000.csv' using (bin($1/1000000,binwidth)):(1.0) smooth freq with boxes title '300 Additionen'
plot 'slow_100000.csv' using (bin($1/1000000,binwidth)):(1.0) smooth freq with boxes title '301 Additionen'

unset multiplot
unset output
