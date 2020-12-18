set encoding iso_8859_1
set terminal pdf size 10,5

set out 'heatmap_parallel.pdf'
set multiplot layout 1,3 margins 0.1,0.90,.15,.90 spacing 0,0

set cbrange [0:100]

unset key

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterationen'
#set cblabel 'Sensitivit{\344}t'
set cblabel 'F1-Ma{\337}'

unset colorbox

set yrange [0:500]
set title 'Sequentiell'
plot 'sequential_10000.csv' u 1:2:3 with image notitle

unset ylabel
unset ytics

set yrange [0:500]
set title 'Parallel'
plot 'parallel_10000.csv' u 1:2:3 with image notitle

set colorbox

set yrange [0:500]
set title 'Mit St{\366}rung'
plot 'noise_10000.csv' u 1:2:3 with image notitle

unset multiplot
unset output

