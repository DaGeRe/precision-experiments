set encoding iso_8859_1

set terminal pdf size 11,3
set out 'heatmap_all_de.pdf'
set multiplot layout 1,5 margins 0.075,0.94,.125,.92 spacing 0.04,0

set cbrange [0:100]

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterationen'
#set cblabel 'Sensitivit{\344}t'


unset colorbox
unset cblabel

set yrange [0:50000]
set title '100 Wiederholungen'
plot 'noOutlierRemoval_100.csv' u 1:2:3 with image title ''

unset ylabel

set title '1.000 Wiederholungen'
set yrange [0:5000]
plot 'noOutlierRemoval_1000.csv' u 1:2:3 with image title ''

set title '10.000 Wiederholungen'
set yrange [0:500]
plot 'noOutlierRemoval_10000.csv' u 1:2:3 with image title ''

set title '100.000 Wiederholungen'
set yrange [0:50]
plot 'noOutlierRemoval_100000.csv' u 1:2:3 with image title ''

set colorbox
set cblabel 'F1-Ma{\337}'

set title '1.000.000 Wiederholungen'
set yrange [0:5]
plot 'noOutlierRemoval_1000000.csv' u 1:2:3 with image title ''
unset multiplot
unset output


set terminal pdf size 11,3
set out 'heatmap_all_en.pdf'
set multiplot layout 1,5 margins 0.075,0.94,.125,.92 spacing 0.04,0

set cbrange [0:100]

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterations'

unset colorbox
unset cblabel

set yrange [0:50000]
set title '100 Repetitions'
plot 'noOutlierRemoval_100.csv' u 1:2:3 with image title ''

unset ylabel

set title '1,000 Repetitions'
set yrange [0:5000]
plot 'noOutlierRemoval_1000.csv' u 1:2:3 with image title ''

set title '10,000 Repetitions'
set yrange [0:500]
plot 'noOutlierRemoval_10000.csv' u 1:2:3 with image title ''

set title '100,000 Repetitions'
set yrange [0:50]
plot 'noOutlierRemoval_100000.csv' u 1:2:3 with image title ''

set colorbox
set cblabel 'F1 Score'

set title '1,000,000 Repetitions'
set yrange [0:5]
plot 'noOutlierRemoval_1000000.csv' u 1:2:3 with image title ''
unset multiplot
unset output

