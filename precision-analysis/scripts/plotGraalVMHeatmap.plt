set encoding iso_8859_1
set terminal pdf size 10,5

set out 'result_meanTTest.pdf'
set multiplot layout 1,2

set cbrange [0:100]

set xrange [0:31]
set xlabel 'VMs'
set ylabel 'Iterationen'
set cblabel 'F_1-Ma{\337}'

set title 'Mittelwertvergleich'
set yrange [0:8]
unset colorbox
plot '100k_mean.csv' u 1:2:3 with image

set title 'T-Test'
set colorbox
unset ylabel
plot '100k_ttest.csv' u 1:2:3 with image

unset multiplot
unset output

set out 'result_other.pdf'
set multiplot layout 1,2

set title 'Konfidenzintervallvergleich'
unset colorbox
plot '100k_confidence.csv' u 1:2:3 with image

set title 'Mann-Whitney-Test
unset ylabel
set colorbox
plot '100k_mann.csv' u 1:2:3 with image

unset multiplot
unset output


set out 'result_tests_en.pdf'
set multiplot layout 1,3

set cbrange [0:100]

set xrange [1:31]
set xlabel 'VMs'
set ylabel 'Iterations'
set cblabel 'F_1 Score'

set title 'Mean Comparison'
set yrange [1:8]
unset colorbox
plot '100k_mean.csv' u 1:2:3 with image title 'Mean'

set title 'T-Test'
unset ylabel
plot '100k_ttest.csv' u 1:2:3 with image title 'T-Test'

set title 'Mann-Whitney-Test'
unset ylabel
set colorbox
plot '100k_mann.csv' u 1:2:3 with image title 'Mann-Whitney'

unset multiplot
unset output


set out 'result_ttest.pdf'
set terminal pdf size 5,6
set title 'T-Test'
unset ylabel
plot '100k_ttest.csv' u 1:2:3 with image title 'T-Test'
unset output
