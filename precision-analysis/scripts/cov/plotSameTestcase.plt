set encoding iso_8859_1
set terminal pdf size 5,4

set out 'sameTestcase_de.pdf'

set multiplot layout 2,1
set xrange [0:100]

set decimalsign ','

set table $VALUES_499
plot 'AddTest/aggregated/result_499_add_all.csv' u 0:($1/1000)
unset table

set table $VALUES_500
plot 'AddTest/aggregated/result_500_add_all.csv' u 0:($1/1000)
unset table

set table $VALUES_501
plot 'AddTest/aggregated/result_501_add_all.csv' u 0:($1/1000)
unset table

set table $DEVIATIONS_499
plot 'AddTest/aggregated/result_499_add_all.csv' u 0:2
unset table

set table $DEVIATIONS_500
plot 'AddTest/aggregated/result_500_add_all.csv' u 0:2
unset table

set table $DEVIATIONS_501
plot 'AddTest/aggregated/result_501_add_all.csv' u 0:2
unset table

set decimalsign locale 'de_DE.utf8'

set ylabel 'Dauer / {/Symbol m}s'
plot $VALUES_500 w linespoint title 'AddTest 500', $VALUES_499 w linespoint title 'AddTest 499', $VALUES_501 w linespoint title 'AddTest 501'

set ylabel 'Relative Standardabweichung'
set logscale y
unset key
plot $DEVIATIONS_500 u 0:2 w linespoint title 'AddTest', $DEVIATIONS_499 u 0:2 w linespoint title 'AddTest', $DEVIATIONS_501 w linespoint title 'AddTest'
unset logscale y

unset multiplot
unset out
