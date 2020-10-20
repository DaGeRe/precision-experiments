set encoding iso_8859_1
set terminal pdf size 5,3
set key top center

set out 'evaluation_comparison.pdf'
set xlabel 'VMs'
set ylabel 'F1-Ma{\337}'
plot '1mio_5.csv' u 1:3 w linespoint title 'PeASS', 'precision_junit.csv' u 2:15 title 'JUnit' w linespoint, 'precision_bench.csv' u 2:15 title 'Benchmark' w linespoint

unset out

set key left

set style fill solid 0.5

binwidth=0.5
set boxwidth binwidth

set encoding iso_8859_1
set terminal pdf size 5,3

set out 'histogram_junit.pdf'

set xlabel 'Dauer / ms'
set ylabel 'Frequenz'

set multiplot layout 2,1
plot 'avg_junit_300.csv' using (bin($1/1000,binwidth)):(1.0) smooth freq with boxes title '300 Additionen'
plot 'avg_junit_301.csv' using (bin($1/1000,binwidth)):(1.0) smooth freq with boxes title '301 Additionen'

unset multiplot
unset out

set out 'histogram_bench.pdf'

set key right
binwidth=0.25
set boxwidth binwidth

set xrange [122:130]

set xlabel 'Dauer / ms'
set ylabel 'Frequenz'

set multiplot layout 2,1
plot 'avg_bench_300.csv' using (bin($1/1000,binwidth)):(1.0) smooth freq with boxes title '300 Additionen'
plot 'avg_bench_301.csv' using (bin($1/1000,binwidth)):(1.0) smooth freq with boxes title '301 Additionen'

unset multiplot
unset out
