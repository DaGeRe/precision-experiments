set encoding iso_8859_1
set terminal pdf size 13,4

set out 'heatmap_allWorkloads.pdf'
set multiplot layout 1,4 margins 0.1,0.95,0.1,0.92 spacing 0.03,0

set cbrange [0:100]

set xrange [0:1000]
set xlabel 'VMs'
set cblabel 'F1 Score'
set ylabel 'Iterations'
unset colorbox

set yrange [0:50000]
set title '100 Repetitions'
plot 'sizes/100.csv' u 1:2:3 with image

unset ylabel

set title '1 000 Repetitions'
set yrange [0:5000]
plot 'sizes/1000.csv' u 1:2:3 with image

set title '10.000 Repetitions'
set yrange [0:500]
plot 'sizes/10000.csv' u 1:2:3 with image

set colorbox

set title '100 000 Repetitions'
set yrange [0:50]
plot 'sizes/100000.csv' u 1:2:3 with image

unset multiplot
unset output

