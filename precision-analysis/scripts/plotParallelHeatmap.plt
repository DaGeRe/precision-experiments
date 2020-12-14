set encoding iso_8859_1
set terminal pdf size 10,5

set out 'heatmap_parallel.pdf'
set multiplot layout 1,3

set cbrange [0:100]

unset key

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterationen'
#set cblabel 'Sensitivit{\344}t'
set cblabel 'F1-Ma{\337}'

set yrange [0:500]
set title 'Sequentiell'
plot 'sequential_10000.csv' u 1:2:3 with image

set yrange [0:500]
set title 'Parallel'
plot 'parallel_10000.csv' u 1:2:3 with image

set yrange [0:500]
set title 'Mit St{\366}rung'
plot 'noise_10000.csv' u 1:2:3 with image

unset multiplot
unset output

