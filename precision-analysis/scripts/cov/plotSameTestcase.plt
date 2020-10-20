set encoding iso_8859_1
set terminal pdf size 5,3

set out 'sameTestcase.pdf

set multiplot layout 2,1
set xrange [0:100]

set ylabel 'Dauer / ns'
plot 'AddTest/aggregated/result_500_add_all.csv' u 0:1 w linespoint title 'AddTest 500', 'AddTest/aggregated/result_499_add_all.csv' u 0:1 w linespoint title 'AddTest 499', 'AddTest/aggregated/result_501_add_all.csv' u 0:1 w linespoint title 'AddTest 501'

set ylabel 'Relative Standardabweichung'
set logscale y
unset key
plot 'AddTest/aggregated/result_500_add_all.csv' u 0:2 w linespoint title 'AddTest', 'AddTest/aggregated/result_499_add_all.csv' u 0:2 w linespoint title 'AddTest', 'AddTest/aggregated/result_501_add_all.csv' u 0:2 w linespoint title 'AddTest'
unset logscale y

unset multiplot
unset out
