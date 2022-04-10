set out 'sysout_comparison_de.pdf'
set encoding iso_8859_1
set terminal pdf size 5,3

set encoding iso_8859_1
set xlabel 'Workloadgr{\366}{\337}e'
set ylabel 'Dauer in {/Symbol m}s'
set xrange [0:2001]

set decimalsign ','

set table $WITHOUT
plot 'without.csv' u 1:($2/1000)
unset table

set table $MAVEN
plot 'withMvn.csv' u 1:($2/1000)
unset table

set table $GRADLE
plot 'withGradle.csv' u 1:($2/1000)
unset table

set decimalsign locale 'de_DE.utf8'
set format x "%'.0f"

plot $WITHOUT w linespoint title 'Deaktivierte Standardausgabe', $MAVEN w linespoint title 'Aktivierte Standardausgabe (mvn)', $GRADLE w linespoint title 'Aktivierte Standardausgabe (Gradle)'

unset out
