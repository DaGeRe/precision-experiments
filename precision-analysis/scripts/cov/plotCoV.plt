set encoding iso_8859_1
set terminal pdf size 8,10

set out 'covs.pdf

set multiplot layout 3,2
set xrange [0:1000]

set ylabel 'Dauer / {/Symbol m}s'

set decimalsign ','

set table $VALUES_ADDTEST
plot 'AddTest/aggregated/result_500_add_all.csv' u 0:($1/1000)
unset table

set table $DEVIATIONS_ADDTEST
plot 'AddTest/aggregated/result_500_add_all.csv' u 0:2
unset table

set table $VALUES_RAMTEST
plot 'RAMTest/aggregated/result_500_add_all.csv' u 0:($1/1000)
unset table

set table $DEVIATIONS_RAMTEST
plot 'RAMTest/aggregated/result_500_add_all.csv' u 0:2
unset table

set table $VALUES_SYSOUTTEST
plot 'SysoutTest/aggregated/result_500_sysout_all.csv' u 0:($1/1000)
unset table

set table $DEVIATIONS_SYSOUTTEST
plot 'SysoutTest/aggregated/result_500_sysout_all.csv' u 0:2
unset table

set decimalsign locale 'de_DE.utf8'

plot $VALUES_ADDTEST w linespoint title 'AddTest'

set ylabel 'Relative Standardabweichung'
set logscale y
plot $DEVIATIONS_ADDTEST w linespoint title 'AddTest'
unset logscale y

set ylabel 'Dauer / {/Symbol m}s'
plot $VALUES_RAMTEST w linespoint title 'RAMTest'

set ylabel 'Relative Standardabweichung'
set logscale y
plot $DEVIATIONS_RAMTEST w linespoint title 'RAMTest'
unset logscale y

set xlabel 'Iteration'

set ylabel 'Dauer / {/Symbol m}s'
plot $VALUES_SYSOUTTEST w linespoint title 'SysoutTest'

set ylabel 'Relative Standardabweichung'
set logscale y
plot $DEVIATIONS_SYSOUTTEST w linespoint title 'SysoutTest'
unset logscale y

unset multiplot
unset out
