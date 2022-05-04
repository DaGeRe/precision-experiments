set terminal pdf size 6,3
set out 'callCountHistogram_de.pdf'

set boxwidth binwidth
bin(x,width)=width*floor(x/width) + width/2.0

set style fill solid 0.5

set xrange [0:1000]

set xlabel 'Anzahl der Methodenaufrufe'
set ylabel 'Häufigkeit'

plot 'all.csv' u (bin($1,binwidth)):(1.0) smooth freq with boxes title 'Methodenaufrufe'

unset output
