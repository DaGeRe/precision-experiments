set encoding iso_8859_1
set terminal pdf size 8,10

set out 'covs.pdf

set multiplot layout 3,2
set xrange [0:1000]

set ylabel 'Dauer / ns'
plot 'AddTest/aggregated/result_500_add_all.csv' u 0:1 w linespoint title 'AddTest'

set ylabel 'Relative Standardabweichung'
set logscale y
plot 'AddTest/aggregated/result_500_add_all.csv' u 0:2 w linespoint title 'AddTest'
unset logscale y

set ylabel 'Dauer / ns'
plot 'RAMTest/aggregated/result_500_add_all.csv' u 0:1 w linespoint title 'RAMTest'

set ylabel 'Relative Standardabweichung'
set logscale y
plot 'RAMTest/aggregated/result_500_add_all.csv' u 0:2 w linespoint title 'RAMTest'
unset logscale y

set xlabel 'Iteration'

set ylabel 'Dauer / ns'
plot 'SysoutTest/aggregated/result_500_sysout_all.csv' u 0:1 w linespoint title 'SysoutTest'

set ylabel 'Relative Standardabweichung'
set logscale y
plot 'SysoutTest/aggregated/result_500_sysout_all.csv' u 0:2 w linespoint title 'SysoutTest'
unset logscale y

unset multiplot
unset out
