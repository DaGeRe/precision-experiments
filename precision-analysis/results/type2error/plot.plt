set encoding iso_8859_1
set terminal pdf size 4,2

set out 'type2error_automatedExamination.pdf'

set title 'Evolution of Type II error'

set xlabel 'VMs'
set ylabel 'Type II error'

plot 'error_01.csv' w lines title 'r=0.1', 'error_025.csv' w lines title 'r=0.25', 'error_05.csv' w lines title 'r=0.5', 'error_1.csv' w lines title 'r=1'

unset out
