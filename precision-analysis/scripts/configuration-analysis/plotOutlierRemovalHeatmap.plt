maximum=5000

set encoding iso_8859_1
set terminal pdf size 3,4

set out 'heatmap_outlierRemoval_TTEST_de.pdf'
set multiplot layout 2,1

set cbrange [0:100]

unset key

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterationen'
#set cblabel 'Sensitivit{\344}t'
set cblabel 'F1-Ma{\337}'


set yrange [0:maximum]
set title 'Keine Ausrei{\337}erentfernung'
plot 'noOutlierRemoval_1000_TTEST.csv' u 1:2:3 with image notitle

set yrange [0:maximum]
set title 'Mit Ausrei{\337}erentfernung'
plot 'outlierRemoval_1000_TTEST.csv' u 1:2:3 with image notitle

unset multiplot
unset output




set encoding iso_8859_1
set terminal pdf size 3,4

set out 'heatmap_outlierRemoval_MANNWHITNEY_de.pdf'
set multiplot layout 2,1

set cbrange [0:100]

unset key

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterationen'
set cblabel 'F_1-Ma{\337}'


set yrange [0:500]
set title 'Keine Ausrei{\337}erentfernung'
plot 'noOutlierRemoval_10000_MANNWHITNEY.csv' u 1:2:3 with image notitle

set yrange [0:500]
set title 'Mit Ausrei{\337}erentfernung'
plot 'outlierRemoval_10000_MANNWHITNEY.csv' u 1:2:3 with image notitle

unset multiplot
unset output




set encoding iso_8859_1
set terminal pdf size 3,4

set out 'heatmap_outlierRemoval_MANNWHITNEY_en.pdf'
set multiplot layout 2,1

set cbrange [0:100]

unset key

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterationen'
set cblabel 'F_1-score'

set xtics ("0" 0, "200" 200, "400" 400, "600" 600, "800" 800, "1,000" 1000 )

set yrange [0:50]
set title 'No Outlier Removal'
plot 'noOutlierRemoval_100000_MANNWHITNEY.csv' u 1:2:3 with image notitle

set yrange [0:50]
set title 'Outier Removal'
plot 'outlierRemoval_100000_MANNWHITNEY.csv' u 1:2:3 with image notitle

unset multiplot
unset output






set encoding iso_8859_1
set terminal pdf size 5,3


# Here starts the english part (exactly the same as above, but with english labels)
set out 'heatmap_outlierRemoval_TTEST_en.pdf'
set multiplot layout 2,1

set cbrange [0:100]

unset key

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterations'
#set cblabel 'Sensitivity'
set cblabel 'F1-Score'


set yrange [0:maximum]
set title 'No Outlier Removal'
plot 'noOutlierRemoval_1000_TTEST.csv' u 1:2:3 with image notitle

set yrange [0:maximum]
set title 'Outlier Removal'
plot 'outlierRemoval_1000_TTEST.csv' u 1:2:3 with image notitle

unset multiplot
unset output



set out 'heatmap_outlierRemoval_AddTest_TTEST_en.pdf'
set multiplot layout 2,1

set cbrange [0:100]

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterations'
set cblabel 'F1-Score'

set yrange [0:maximum]
set title 'No Outlier Removal'
plot 'noOutlierRemoval_AddTest_1000_TTEST.csv' u 1:2:3 with image notitle

set yrange [0:maximum]
set title 'Outlier Removal'
plot 'outlierRemoval_AddTest_1000_TTEST.csv' u 1:2:3 with image notitle

unset multiplot
unset output


set out 'heatmap_outlierRemoval_RAMTest_TTEST_en.pdf'
set multiplot layout 2,1

set cbrange [0:100]

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterations'
set cblabel 'F1-Score'

set yrange [0:maximum]
set title 'No Outlier Removal'
plot 'noOutlierRemoval_RAMTest_1000_TTEST.csv' u 1:2:3 with image notitle

set yrange [0:maximum]
set title 'Outlier Removal'
plot 'outlierRemoval_RAMTest_1000_TTEST.csv' u 1:2:3 with image notitle

unset multiplot
unset output


set out 'heatmap_outlierRemoval_SysoutTest_TTEST_en.pdf'
set multiplot layout 2,1

set cbrange [0:100]

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterations'
set cblabel 'F1-Score'

set yrange [0:maximum]
set title 'No Outlier Removal'
plot 'noOutlierRemoval_SysoutTest_1000_TTEST.csv' u 1:2:3 with image notitle

set yrange [0:maximum]
set title 'Outlier Removal'
plot 'outlierRemoval_SysoutTest_1000_TTEST.csv' u 1:2:3 with image notitle

unset multiplot
unset output

