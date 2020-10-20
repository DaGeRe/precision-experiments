set multiplot layout 4,1
set x2tics
plot 'vals_25.csv' using (bin($1,binwidth)):(1.0) smooth freq with boxes title 'Fast values 25', 'vals_25.csv' using (bin($3,binwidth)):(1.0) smooth freq with boxes axis x2y1
plot 'vals_25.csv' using (bin($2,binwidth)):(1.0) smooth freq with boxes title 'Slow values 25', 'vals_25.csv' using (bin($4,binwidth)):(1.0) smooth freq with boxes axis x2y1
plot 'vals_30.csv' using (bin($1,binwidth)):(1.0) smooth freq with boxes title 'Fast values 30', 'vals_30.csv' using (bin($3,binwidth)):(1.0) smooth freq with boxes axis x2y1
plot 'vals_30.csv' using (bin($2,binwidth)):(1.0) smooth freq with boxes title 'Slow values 30', 'vals_30.csv' using (bin($4,binwidth)):(1.0) smooth freq with boxes axis x2y1
plot 'vals_1000.csv' using (bin($2,binwidth)):(1.0) smooth freq with boxes


set multiplot layout 4,1
set xrange [1,2e8:1,24e8]
plot 'vals_25.csv' using (bin($1,binwidth)):(1.0) smooth freq with boxes title 'Fast values 25'
plot 'vals_25.csv' using (bin($2,binwidth)):(1.0) smooth freq with boxes title 'Slow values 25'
plot 'vals_30.csv' using (bin($1,binwidth)):(1.0) smooth freq with boxes title 'Fast values 30'
plot 'vals_30.csv' using (bin($2,binwidth)):(1.0) smooth freq with boxes title 'Slow values 30'

plot 'vals_30.csv' u ($2-$1):($3+$4):5 w points palette

plot 'vals_25.csv' u (abs($1-$2)/sqrt($3+$4)):6:5 w points palette

set multiplot layout 4,1
plot 'vals_15.csv' u (bin($6,binwidth)):(1.0) smooth freq with boxes
plot 'vals_20.csv' u (bin($6,binwidth)):(1.0) smooth freq with boxes
plot 'vals_25.csv' u (bin($6,binwidth)):(1.0) smooth freq with boxes
plot 'vals_1000.csv' u (bin($6,binwidth)):(1.0) smooth freq with boxes

