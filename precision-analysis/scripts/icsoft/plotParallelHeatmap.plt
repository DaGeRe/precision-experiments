set encoding iso_8859_1
set terminal pdf size 3,4

set out 'heatmap_parallel_de.pdf'
set multiplot layout 2,1

set cbrange [0:100]

unset key

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterationen'
#set cblabel 'Sensitivit{\344}t'
set cblabel 'F1-Ma{\337}'


set yrange [0:50]
set title 'Sequenziell'
plot 'repetitionHeatmaps/noOutlierRemoval_100000.csv' u 1:2:3 with image notitle

set yrange [0:50]
set title 'Parallel'
plot 'repetitionHeatmapsParallel/noOutlierRemoval_100000.csv' u 1:2:3 with image notitle

unset multiplot
unset output

# Here starts the english part (exactly the same as above, but with english labels)

set out 'heatmap_parallel_en.pdf'
set multiplot layout 2,1

set cbrange [0:100]

unset key

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterations'
#set cblabel 'Sensitivity'
set cblabel 'F1-Score'


set yrange [0:50]
set title 'Sequential'
plot 'repetitionHeatmaps/noOutlierRemoval_100000.csv' u 1:2:3 with image notitle

set yrange [0:50]
set title 'Parallel'
plot 'repetitionHeatmapsParallel/noOutlierRemoval_100000.csv' u 1:2:3 with image notitle

unset multiplot
unset output

