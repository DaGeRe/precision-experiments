set encoding iso_8859_1
set terminal pdf size 5,3
set xlabel 'Workloadgr{\366}{\337}e'
set ylabel 'Relative Standard Deviation'
set y2tics
set y2label 'Absolute Standard Deviation'
set key top center

#set decimalsign locale 
#set format "%'.0f"

set out 'vmdeviation_both.pdf'

set xrange [0:1000]

set logscale x
set logscale y2
set style data linespoints
plot 'AddTest/vmdeviation_evolution.csv' title 'AddTest', \
	'RAMTest/vmdeviation_evolution.csv' title 'RAMTest',  \
	'SysoutTest/vmdeviation_evolution.csv' title 'SysoutTest', \
	'AddTest/vmdeviation_evolution_absolute.csv' title 'AddTest' axis x1y2, \
	'RAMTest/vmdeviation_evolution_absolute.csv' title 'RAMTest' axis x1y2, \
	'SysoutTest/vmdeviation_evolution_absolute.csv' title 'SysoutTest' axis x1y2

unset out
