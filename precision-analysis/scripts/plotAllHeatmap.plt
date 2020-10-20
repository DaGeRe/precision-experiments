set encoding iso_8859_1
set terminal pdf size 10,13

set out 'heatmap_all.pdf'
set multiplot layout 5,1

set cbrange [0:100]

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterationen'
#set cblabel 'Sensitivit{\344}t'
set cblabel 'F1-Ma{\337}'

set yrange [0:50000]
set title '100 Wiederholungen'
plot '100.csv' u 1:2:3 with image

set title '1.000 Wiederholungen'
set yrange [0:5000]
plot '1000.csv' u 1:2:3 with image

set title '10.000 Wiederholungen'
set yrange [0:500]
plot '10000.csv' u 1:2:3 with image

set title '100.000 Wiederholungen'
set yrange [0:50]
plot '100000.csv' u 1:2:3 with image

set title '1.000.000 Wiederholungen'
set yrange [0:5]
plot '1000000.csv' u 1:2:3 with image
unset multiplot
unset output

