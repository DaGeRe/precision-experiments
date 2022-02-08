set encoding iso_8859_1
set terminal pdf size 5,6

set out 'heatmap_outlier.pdf'
set multiplot layout 2,1

set cbrange [0:100]

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterationen'
set cblabel 'F1 Score'

set yrange [0:5000]
set title 'No Outlier Removal'
plot 'noOutlierRemoval.csv' u 1:2:3 with image

set title 'Outlier Removal'
plot 'outlierRemoval.csv' u 1:2:3 with image

unset multiplot
unset output

