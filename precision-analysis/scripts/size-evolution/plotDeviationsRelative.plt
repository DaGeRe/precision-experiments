set encoding iso_8859_1
set terminal pdf size 5,3
set xlabel 'Workloadgr{\366}{\337}e'
set ylabel 'Relative Standardabweichung'
set key right center

#set decimalsign locale 
#set format "%'.0f"

set out 'vmdeviation_relative_de.pdf'

set logscale x
set xtics ("1" 1, "10" 10, "100" 100, "1.000" 1000, "10.000" 10000, "100.000" 100000, "1x10^6" 1000000, "1x10^7" 10000000)
set ytics ("0" 0, "0,02" 0.02, "0,04" 0.04, "0,06" 0.06, "0,08" 0.08, "0,10" 0.10, "0,12" 0.12)
set yrange [0:0.12]

set style data linespoints
plot 'AddTest/vmdeviation_evolution.csv' title 'AddTest', 'RAMTest/vmdeviation_evolution.csv' title 'RAMTest', 'SysoutTest/vmdeviation_evolution.csv' title 'SysoutTest', 'BaselineTest/vmdeviation_evolution.csv' title 'BaselineTest'

unset out


set terminal pdf size 5,3
set xlabel 'Workload Size'
set ylabel 'Relative Deviation'
set key top right

#set decimalsign locale 
#set format "%'.0f"

set out 'vmdeviation_relative_en.pdf'

set logscale x
set xtics ("1" 1, "10" 10, "100" 100, "1,000" 1000, "10,000" 10000, "100,000" 100000, "1x10^6" 1000000, "1x10^7" 10000000)
set ytics border mirror norotate autofreq
set yrange [0:0.04]

set style data linespoints
plot 'AddTest/vmdeviation_evolution.csv' title 'AddTest', 'RAMTest/vmdeviation_evolution.csv' title 'RAMTest', 'SysoutTest/vmdeviation_evolution.csv' title 'SysoutTest'

# Do not plot baseline test since it is not required in conference publications - add if needed: 'BaselineTest/vmdeviation_evolution.csv' title 'BaselineTest'

unset out
