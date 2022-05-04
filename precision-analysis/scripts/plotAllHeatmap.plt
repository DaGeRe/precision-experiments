set encoding iso_8859_1

set terminal pdf size 11,3
set out 'heatmap_all_de_MEAN.pdf'
set multiplot layout 1,5 margins 0.075,0.94,.125,.92 spacing 0.04,0

set cbrange [0:100]

set xtics ("0" 0, "200" 200, "400" 400, "600" 600, "800" 800, "1.000" 1000 )

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterationen'
#set cblabel 'Sensitivit{\344}t'

unset colorbox
unset cblabel

set yrange [0:50000]
set ytics ("0" 0, "10.000" 10000, "20.000" 20000, "30.000" 30000, "40.000" 40000, "50.000" 50000)

set title '100 Wiederholungen'
plot 'noOutlierRemoval_100_MEAN.csv' u 1:2:3 with image title ''

unset ylabel

set title '1.000 Wiederholungen'
set yrange [0:5000]
set ytics ("0" 0, "1.000" 1000, "2.000" 2000, "3.000" 3000, "4.000" 4000, "5.000" 5000)

plot 'noOutlierRemoval_1000_MEAN.csv' u 1:2:3 with image title ''

set title '10.000 Wiederholungen'
set yrange [0:500]
unset ytics
set ytics 

plot 'noOutlierRemoval_10000_MEAN.csv' u 1:2:3 with image title ''

set title '100.000 Wiederholungen'
set yrange [0:50]
plot 'noOutlierRemoval_100000_MEAN.csv' u 1:2:3 with image title ''

set colorbox
set cblabel 'F_1-Ma{\337}'

set title '1.000.000 Wiederholungen'
set yrange [0:5]
plot 'noOutlierRemoval_1000000_MEAN.csv' u 1:2:3 with image title ''
unset multiplot
unset output





set terminal pdf size 11,3
set out 'heatmap_all_de_TTEST.pdf'
set multiplot layout 1,5 margins 0.075,0.94,.125,.92 spacing 0.04,0

set cbrange [0:100]

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterationen'

unset colorbox
unset cblabel

set yrange [0:50000]
set ytics ("0" 0, "10.000" 10000, "20.000" 20000, "30.000" 30000, "40.000" 40000, "50.000" 50000)

set title '100 Wiederholungen'
plot 'noOutlierRemoval_100_TTEST.csv' u 1:2:3 with image title ''

unset ylabel

set title '1.000 Wiederholungen'
set yrange [0:5000]
set ytics ("0" 0, "1.000" 1000, "2.000" 2000, "3.000" 3000, "4.000" 4000, "5.000" 5000)

plot 'noOutlierRemoval_1000_TTEST.csv' u 1:2:3 with image title ''

set title '10.000 Wiederholungen'
set yrange [0:500]
unset ytics
set ytics 

plot 'noOutlierRemoval_10000_TTEST.csv' u 1:2:3 with image title ''

set title '100.000 Wiederholungen'
set yrange [0:50]
plot 'noOutlierRemoval_100000_TTEST.csv' u 1:2:3 with image title ''

set colorbox
set cblabel 'F_1-Ma{\337}'

set title '1.000.000 Wiederholungen'
set yrange [0:5]
plot 'noOutlierRemoval_1000000_TTEST.csv' u 1:2:3 with image title ''
unset multiplot
unset output





set terminal pdf size 11,3
set out 'heatmap_all_de_CONFIDENCE.pdf'
set multiplot layout 1,5 margins 0.075,0.94,.125,.92 spacing 0.04,0

set cbrange [0:100]

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterationen'

unset colorbox
unset cblabel

set yrange [0:50000]
set ytics ("0" 0, "10.000" 10000, "20.000" 20000, "30.000" 30000, "40.000" 40000, "50.000" 50000)
set title '100 Wiederholungen'
plot 'noOutlierRemoval_100_CONFIDENCE.csv' u 1:2:3 with image title ''

unset ylabel

set title '1.000 Wiederholungen'
set yrange [0:5000]
set ytics ("0" 0, "1.000" 1000, "2.000" 2000, "3.000" 3000, "4.000" 4000, "5.000" 5000)
plot 'noOutlierRemoval_1000_CONFIDENCE.csv' u 1:2:3 with image title ''

set title '10.000 Wiederholungen'
set yrange [0:500]
unset ytics
set ytics 
plot 'noOutlierRemoval_10000_CONFIDENCE.csv' u 1:2:3 with image title ''

set title '100.000 Wiederholungen'
set yrange [0:50]
plot 'noOutlierRemoval_100000_CONFIDENCE.csv' u 1:2:3 with image title ''

set colorbox
set cblabel 'F_1-Ma{\337}'

set title '1.000.000 Wiederholungen'
set yrange [0:5]
plot 'noOutlierRemoval_1000000_CONFIDENCE.csv' u 1:2:3 with image title ''
unset multiplot
unset output





set terminal pdf size 11,3
set out 'heatmap_all_de_MANNWHITNEY.pdf'
set multiplot layout 1,5 margins 0.075,0.94,.125,.92 spacing 0.04,0

set cbrange [0:100]

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterationen'

unset colorbox
unset cblabel

set yrange [0:50000]
set ytics ("0" 0, "10.000" 10000, "20.000" 20000, "30.000" 30000, "40.000" 40000, "50.000" 50000)
set title '100 Wiederholungen'
plot 'noOutlierRemoval_100_MANNWHITNEY.csv' u 1:2:3 with image title ''

unset ylabel

set title '1.000 Wiederholungen'
set yrange [0:5000]
set ytics ("0" 0, "1.000" 1000, "2.000" 2000, "3.000" 3000, "4.000" 4000, "5.000" 5000)
plot 'noOutlierRemoval_1000_MANNWHITNEY.csv' u 1:2:3 with image title ''

set title '10.000 Wiederholungen'
set yrange [0:500]
unset ytics
set ytics 
plot 'noOutlierRemoval_10000_MANNWHITNEY.csv' u 1:2:3 with image title ''

set title '100.000 Wiederholungen'
set yrange [0:50]
plot 'noOutlierRemoval_100000_MANNWHITNEY.csv' u 1:2:3 with image title ''

set colorbox
set cblabel 'F_1-Ma{\337}'

set title '1.000.000 Wiederholungen'
set yrange [0:5]
plot 'noOutlierRemoval_1000000_MANNWHITNEY.csv' u 1:2:3 with image title ''
unset multiplot
unset output



set terminal pdf size 11,3
set out 'heatmap_all_en_TTEST.pdf'
set multiplot layout 1,5 margins 0.075,0.94,.125,.92 spacing 0.04,0

set cbrange [0:100]

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterations'

unset colorbox
unset cblabel

set yrange [0:50000]
set ytics ("0" 0, "10,000" 10000, "20,000" 20000, "30,000" 30000, "40,000" 40000, "50,000" 50000)
set title '100 Repetitions'
plot 'noOutlierRemoval_100_TTEST.csv' u 1:2:3 with image title ''

unset ylabel

set title '1,000 Repetitions'
set yrange [0:5000]
set ytics ("0" 0, "1,000" 1000, "2,000" 2000, "3,000" 3000, "4,000" 4000, "5,000" 5000)
plot 'noOutlierRemoval_1000_TTEST.csv' u 1:2:3 with image title ''

set title '10,000 Repetitions'
set yrange [0:500]
plot 'noOutlierRemoval_10000_TTEST.csv' u 1:2:3 with image title ''

set title '100,000 Repetitions'
set yrange [0:50]
plot 'noOutlierRemoval_100000_TTEST.csv' u 1:2:3 with image title ''

set colorbox
set cblabel 'F_1 Score'

set title '1,000,000 Repetitions'
set yrange [0:5]
plot 'noOutlierRemoval_1000000_TTEST.csv' u 1:2:3 with image title ''
unset multiplot
unset output

