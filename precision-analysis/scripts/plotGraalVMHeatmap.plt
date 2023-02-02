set encoding iso_8859_1
set terminal pdf size 10,5

set out 'result_meanTTest.pdf'
set multiplot layout 1,2

set cbrange [0:100]

set xrange [0:35]
set xlabel 'VMs'
set ylabel 'Iterationen'
set cblabel 'F1-Ma{\337}'

set title 'Mittelwertvergleich'
set yrange [0:10]
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

