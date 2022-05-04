set encoding iso_8859_1
set terminal pdf size 5,3

set out 'vmdeviation.pdf'

set xlabel 'Workloadgr{\366}{\337}e'
set ylabel 'Absolute Standardabweichung'
set logscale x
set logscale y
set style data linespoints
plot 'AddTest/vmdeviation_evolution_absolute.csv' title 'AddTest', 'RAMTest/vmdeviation_evolution_absolute.csv' title 'RAMTest', 'SysoutTest/vmdeviation_evolution_absolute.csv' title 'SysoutTest', 'BaselineTest/vmdeviation_evolution_absolute.csv' title 'BaselineTest'

unset out
