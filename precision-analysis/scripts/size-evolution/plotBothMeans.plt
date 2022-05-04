set encoding iso_8859_1
set terminal pdf size 5,3
set xlabel 'Workload Size'
set ylabel 'Relative Standard Deviation'
set y2tics
set y2label 'Absolute Standard Deviation'
set key top center

#set decimalsign locale 
#set format "%'.0f"

set out 'vmdeviation_both_means.pdf'

set xrange [1:10000]

set logscale x
set logscale y2
set style data linespoints
plot 'evolution.csv' u 1:2 axis x1y1 title 'Standard Deviation Relative', 'evolution.csv' u 1:3 axis x1y2 title 'Standard Deviation Absolute'

unset out

set style data linespoint
