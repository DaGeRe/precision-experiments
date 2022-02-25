set encoding iso_8859_1
set terminal pdf size 3,4

set out 'heatmap_outlierRemoval_de.pdf'
set multiplot layout 2,1

set cbrange [0:100]

unset key

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterationen'
#set cblabel 'Sensitivit{\344}t'
set cblabel 'F1-Ma{\337}'


set yrange [0:50]
set title 'Keine Ausrei{\337}erentfernung'
plot 'noOutlierRemoval_100000.csv' u 1:2:3 with image notitle

set yrange [0:50]
set title 'Mit Ausrei{\337}erentfernung'
plot 'outlierRemoval_100000.csv' u 1:2:3 with image notitle

unset multiplot
unset output

set encoding iso_8859_1
set terminal pdf size 5,3


# Here starts the english part (exactly the same as above, but with english labels)

set out 'heatmap_outlierRemoval_en.pdf'
set multiplot layout 2,1

set cbrange [0:100]

unset key

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterations'
#set cblabel 'Sensitivity'
set cblabel 'F1-Score'


set yrange [0:50]
set title 'No Outlier Removal'
plot 'noOutlierRemoval_100000.csv' u 1:2:3 with image notitle

set yrange [0:50]
set title 'Outlier Removal'
plot 'outlierRemoval_100000.csv' u 1:2:3 with image notitle

unset multiplot
unset output

