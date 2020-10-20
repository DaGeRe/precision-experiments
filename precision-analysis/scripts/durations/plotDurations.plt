set terminal pdf size 6,3
set out 'result_durations.pdf'

set style data linespoint
set xlabel 'Wiederholungen'
set ylabel 'Dauer / s'
set logscale x
plot 'AddTest.csv' u 1:3 title 'AddTest', 'RAMTest.csv' u 1:3 title 'RAMTest', 'SysoutTest.csv' u 1:3 title 'SysoutTest'

unset output
