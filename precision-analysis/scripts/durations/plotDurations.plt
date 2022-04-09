set terminal pdf size 6,3
set out 'result_durations_de.pdf'

set style data linespoint
set xlabel 'Wiederholungen'
set ylabel 'Dauer / s'
set logscale x

set xtics ("1" 1, "10" 10, "100" 100, "1.000" 1000, "10.000" 10000, "100.000" 100000, "1x10^6" 1000000)

plot 'AddTest.csv' u 1:3 title 'AddTest', 'RAMTest.csv' u 1:3 title 'RAMTest', 'SysoutTest.csv' u 1:3 title 'SysoutTest'

unset output
