set encoding iso_8859_1
set terminal pdf size 6,3

set out 'comparison_AddTest.pdf'
set multiplot layout 1,2

set cbrange [0:100]

set xrange [0:1000]
set xlabel 'VMs'
set ylabel 'Iterationen'
#set cblabel 'Sensitivit{\344}t'
set cblabel 'F1-Ma{\337}'

set yrange [0:50000]
set title 'Mit Ausrei{\337}ern'
set yrange [0:50]

unset colorbox
plot 'AddTest_noRemoval.csv' u 1:2:3 with image notitle

unset ylabel
set colorbox
set title 'Ohne Ausreisser'
plot 'AddTest_removal.csv' u 1:2:3 with image notitle

unset multiplot
unset output



set out 'comparison_RAMTest.pdf'
set multiplot layout 1,2

set yrange [0:50000]
set title 'Mit Ausrei{\337}ern'

set yrange [0:50]
unset colorbox
plot 'RAMTest_noRemoval.csv' u 1:2:3 with image notitle

unset ylabel
set colorbox
set title 'Ohne Ausreisser'
plot 'RAMTest_removal.csv' u 1:2:3 with image notitle

unset multiplot
unset output


set out 'comparison_SysoutTest.pdf'
set multiplot layout 1,2

set yrange [0:50000]
set title 'Mit Ausrei{\337}ern'

set yrange [0:50]
unset colorbox
plot 'SysoutTest_noRemoval.csv' u 1:2:3 with image notitle

unset ylabel
set colorbox
set title 'Ohne Ausreisser'
plot 'SysoutTest_removal.csv' u 1:2:3 with image notitle

unset multiplot
unset output
