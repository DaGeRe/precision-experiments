set terminal pdf size 6,3
set out 'result.pdf'

binwidth=25
set boxwidth binwidth
bin(x,width)=width*floor(x/width) + width/2.0

set style fill transparent solid 0.3 

set ylabel 'Frequency'
set xlabel 'Duration'

#unset xlabel
#unset xtics

plot 'current.csv' using (bin($1/1000000,binwidth)):(1.0) smooth freq with boxes title 'Current',\
  'predecessor.csv' using (bin($1/1000000,binwidth)):(1.0) smooth freq with boxes title 'Predecessor'

unset multiplot
unset output
